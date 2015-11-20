#!/usr/bin/env python
from __future__ import print_function, absolute_import

import argparse
import json
import logging
import os
import random
import scipy

import numpy as np
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor

import parsers
import utils

DEFAULT_TREE_COUNT = 1000
DEFAULT_OUTFILEPATH = 'forest.pkl'
PICKLE_FILE_EXTENSIONS = ('.p', '.pkl', '.pickle')


def grow_forest(X_train, y_train, activity_type, seed=None, num_trees=None, quiet=False):
    kwargs = {
        'n_estimators': num_trees if num_trees is not None else DEFAULT_TREE_COUNT,
        'oob_score': False,
        'n_jobs': -1,
        'random_state': seed if seed is not None else random.SystemRandom().randint(0, 2 ** 32 - 1),
        'verbose': 0 if quiet else 1,
    }
    forest = None
    if activity_type == 'continuous':
        forest = RandomForestRegressor(**kwargs)
    elif activity_type == 'category':
        forest = RandomForestClassifier(**kwargs)
    return forest.fit(X_train, y_train)


if __name__ == '__main__':
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
    parser.add_argument('-q', '--quiet', help='run quietly', action='store_true')
    args = parser.parse_args()

    if args.quiet:
        logging.basicConfig(level=logging.WARNING, format='%(asctime)s %(levelname)s %(message)s')
    else:
        logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')

    X_train = parsers.read_x_file(args.X_FILE)
    logging.info('Descriptor matrix has shape %s', str(X_train.shape))
    y_train = parsers.read_act_file(args.ACT_FILE, args.ACTIVITY_TYPE)
    logging.info('Activity series has length %s', str(y_train.size))

    logging.info('Growing forest...')
    forest = grow_forest(X_train, y_train, args.ACTIVITY_TYPE, num_trees=args.n, seed=args.seed, quiet=args.quiet)
    logging.info('Model building complete.')

    metadata = {
        'seed': forest.get_params()['random_state'],
        'numpy_version': np.__version__,
        'scipy_version': scipy.__version__,
        'working_directory': os.getcwd(),
        'training_x_file': args.X_FILE,
        'training_act_file': args.ACT_FILE,
    }

    outfilepath = args.output if args.output is not None else DEFAULT_OUTFILEPATH
    metadata_outfilepath = args.metadata if args.metadata is not None else outfilepath
    (basename, extension) = os.path.splitext(outfilepath)
    if extension in PICKLE_FILE_EXTENSIONS:
        metadata_outfilepath = basename
    metadata_outfilepath += '.json'
    with open(metadata_outfilepath, 'w') as metadata_outfile:
        json.dump(metadata, metadata_outfile)
    logging.info('Forest metadata written to "%s"', metadata_outfilepath)

    logging.info('Pickling forest classifier object...')
    archive_outfilepath = utils.save_model(forest, outfilepath)
    logging.info('Forest written to "%s"', archive_outfilepath)
