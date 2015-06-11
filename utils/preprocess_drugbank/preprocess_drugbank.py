#!/usr/bin/env python2
from __future__ import print_function, absolute_import
import argparse
from sdf import sdf

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=(
        'Pre-processes Drugbank structure files in SDF format so that they can be uploaded to Chembench.'))
    parser.add_argument('filepath', help='the Drugbank SDF file to be processed')
    args = parser.parse_args()

    compounds = sdf.get_compounds(args.filepath)
    for compound in compounds:
        # use the GENERIC_NAME tag value for a compound's name
        # (because Drugbank's names aren't unique)
        compound.name = compound.tags['GENERIC_NAME']
        print(compound)
