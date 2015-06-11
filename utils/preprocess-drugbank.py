#!/usr/bin/env python2
from __future__ import print_function, unicode_literals
from collections import OrderedDict
from itertools import groupby
import argparse
import codecs
import re

END_OF_STRUCTURE_REGEX = re.compile(r'^M\s+END\s*$')
TAG_NAME_REGEX = re.compile(r'>\s<([^>]+)>')
END_OF_RECORD = '$$$$'


class Compound(object):
    def __init__(self, sdf, name, comments, structure, tags):
        self.sdf = sdf
        self.name = name
        self.comments = comments
        self.structure = structure
        self.tags = tags

    def __str__(self):
        return unicode(self).encode('utf-8')

    def __unicode__(self):
        record = [self.name, self.comments, self.structure]
        for tag_name, tag_value in self.tags.iteritems():
            # XXX last element is for a newline
            record.extend(('> <{0}>'.format(tag_name), tag_value, ''))
        record.append(END_OF_RECORD)
        return '\n'.join(record)


def process_compound(sdf):
    name = sdf[0]
    comments = '\n'.join((sdf[1], sdf[2]))
    structure_start = 3
    structure_end = structure_start
    while not END_OF_STRUCTURE_REGEX.match(sdf[structure_end]):
        structure_end += 1
    structure_end += 1
    structure = '\n'.join(sdf[structure_start:structure_end])

    tags = OrderedDict()
    for key, group in groupby(sdf[structure_end:-1], lambda x: not x.strip()):
        if not key:
            (tag_name_string, tag_value) = list(group)
            m = TAG_NAME_REGEX.match(tag_name_string)
            tag_name = m.group(1)
            tag_value = tag_value.strip()
            tags[tag_name] = tag_value

    return Compound(sdf=sdf, name=name, comments=comments,
        structure=structure, tags=tags)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=(
        'Pre-processes Drugbank structure files in SDF format so that they '
        'can be uploaded to Chembench.'))
    parser.add_argument('filepath',
        help='the Drugbank SDF file to be processed')

    args = parser.parse_args()
    compounds = []
    with codecs.open(args.filepath, 'rU', encoding='utf8') as f:
        record = []
        for line in f:
            line = line.rstrip('\n')
            record.append(line)
            if line.startswith(END_OF_RECORD):
                compound = process_compound(record)
                # use the GENERIC_NAME tag value for a compound's name
                # (because Drugbank's names aren't unique)
                compound.name = compound.tags['GENERIC_NAME']
                print(compound)
                record = []
