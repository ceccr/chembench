#!/usr/bin/env python
from __future__ import print_function, absolute_import
import argparse
import json
import logging
import os
import random

from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor

import parsers
import utils

DEFAULT_TREE_COUNT = 1000
DEFAULT_OUTFILEPATH = 'forest.pkl'
PICKLE_FILE_EXTENSIONS = ('.p', '.pkl', '.pickle')

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')
    parser = argparse.ArgumentParser(description=(
        'Build a random forest model using the descriptor matrix and activity file provided and save it in pickled '
        'form along with its json-formatted metadata.'))

    parser.add_argument('X_FILE', help='the descriptor matrix for the training set')
    parser.add_argument('ACT_FILE', help='the activity labels for the training set')
    parser.add_argument('ACTIVITY_TYPE', metavar='ACTIVITY_TYPE', choices=('continuous', 'category'),
                        help="activity type: 'continuous' or 'category'")

    parser.add_argument('-n', metavar='NUM_TREES', type=int,
                        help='number of trees to grow (default: {})'.format(DEFAULT_TREE_COUNT))
    parser.add_argument('-o', '--output',
                        help='where to save the pickled random forest classifier object (default: "{}")'.format(
                            DEFAULT_OUTFILEPATH))
    parser.add_argument('-m', '--metadata',
                        help='where to save the classifier\'s metadata (default: derived from "-o" option filename)')
    parser.add_argument('-s', '--seed', help='a specific random seed to use', type=int)
    args = parser.parse_args()

    X_train = parsers.read_x_file(args.X_FILE)
    logging.info('Descriptor matrix has shape %s', str(X_train.shape))
    y_train = parsers.read_act_file(args.ACT_FILE, args.ACTIVITY_TYPE)
    logging.info('Activity series has length %s', str(y_train.size))

    seed = args.seed if args.seed else random.SystemRandom().randint(0, 2 ** 32 - 1)
    num_trees = args.n if args.n else DEFAULT_TREE_COUNT
    kwargs = {
        'n_estimators': num_trees,
        'oob_score': False,
        'n_jobs': -1,
        'random_state': seed,
        'verbose': 1,
    }
    forest = None
    if args.ACTIVITY_TYPE == 'continuous':
        forest = RandomForestRegressor(**kwargs)
    elif args.ACTIVITY_TYPE == 'category':
        forest = RandomForestClassifier(**kwargs)
    logging.info('Growing forest...')
    forest.fit(X_train, y_train)

    outfilepath = args.output if args.output else DEFAULT_OUTFILEPATH
    logging.info('Model building complete.')
    logging.info('Pickling forest classifier object...')
    archive_outfilepath = utils.save_model(forest, outfilepath)
    logging.info('Forest written to "{}"'.format(archive_outfilepath))

    metadata = {
        'descriptor_matrix': args.X_FILE,
        'activity_file': args.ACT_FILE,
        'activity_type': args.ACTIVITY_TYPE,
        'seed': seed,
        'num_trees': num_trees,
        'working_directory': os.getcwd(),
    }

    metadata_outfilepath = args.metadata
    if not metadata_outfilepath:
        metadata_outfilepath = outfilepath
        (basename, extension) = os.path.splitext(outfilepath)
        if extension in PICKLE_FILE_EXTENSIONS:
            metadata_outfilepath = basename
        metadata_outfilepath += '.json'
    with open(metadata_outfilepath, 'w') as metadata_outfile:
        json.dump(metadata, metadata_outfile, indent=4)
    logging.info('Forest metadata written to "%s"', metadata_outfilepath)
