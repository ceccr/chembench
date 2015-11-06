#!/usr/bin/env python
from __future__ import print_function, absolute_import
import argparse
import logging
import pandas as pd

import parsers
import utils

DEFAULT_OUTFILEPATH = 'predictions.csv'

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')
    parser = argparse.ArgumentParser(
        description='Predict a test set using the provided pickled random forest classifier object.')

    parser.add_argument('FOREST_FILE', help='the random forest model in Python pickle form (optionally gzipped)')
    parser.add_argument('TEST_X_FILE', help='the descriptor matrix for the test set')

    parser.add_argument('-o', '--output',
                        help='where to save the tab-separated predictions (default: "{}")'.format(DEFAULT_OUTFILEPATH))
    args = parser.parse_args()

    if args.FOREST_FILE.endswith('.gz'):
        logging.info('Loading gzipped pickled forest "%s"', args.FOREST_FILE)
    else:
        logging.info('Loading raw pickled forest "%s"', args.FOREST_FILE)
    forest = utils.load_model(args.FOREST_FILE)
    logging.info('Loading complete.')

    X_test = parsers.read_x_file(args.TEST_X_FILE)
    logging.info('Predicting test set "%s"', args.TEST_X_FILE)
    predictions = pd.Series(forest.predict(X_test), index=X_test.index)

    outfilepath = args.output if args.output else DEFAULT_OUTFILEPATH
    predictions.to_csv(outfilepath, sep='\t')
    logging.info('Predictions saved to "%s"', outfilepath)
