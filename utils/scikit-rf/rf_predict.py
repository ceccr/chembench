#!/usr/bin/env python
from __future__ import print_function, absolute_import

import argparse
import json
import logging
import math
from collections import namedtuple

import pandas as pd
from sklearn import metrics
from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor

import parsers
import utils

DEFAULT_OUTFILEPATH = 'predictions.json'
Prediction = namedtuple('Prediction', ('predictions', 'stats', 'descriptors_used'))


class ActivityAction(argparse.Action):
    CHOICES = ('continuous', 'category')

    def __call__(self, parser, namespace, values, option_string=None):
        assert len(values) == 2
        (activity_file, activity_type) = values
        if activity_type not in self.CHOICES:
            message = ("invalid choice: {0!r} (choose from {1})".format(
                activity_type, ', '.join([repr(action) for action in self.CHOICES])))
            raise argparse.ArgumentError(self, message)
        setattr(namespace, self.dest, values)


class PandasEncoder(json.JSONEncoder):
    def default(self, obj):
        if hasattr(obj, 'to_dict'):
            return obj.to_dict()
        return json.JSONEncoder.default(self, obj)


def predict_dataset(estimator, dataset, activities=None, activity_type=None):
    predictions = pd.Series(estimator.predict(dataset), index=dataset.index)

    descriptors_used = None
    if isinstance(estimator, DecisionTreeClassifier) or isinstance(estimator, DecisionTreeRegressor):
        descriptors = dataset.columns.values.tolist()
        descriptors_used = [descriptors[x] for x in estimator.tree_.feature if x >= 0]

    stats = {}
    if activities is not None and activity_type is not None:
        if activity_type == 'continuous':
            stats['r_squared'] = metrics.r2_score(activities, predictions)
            stats['mse'] = metrics.mean_squared_error(activities, predictions)
            stats['rmse'] = math.sqrt(stats['mse'])
            stats['mae'] = metrics.mean_absolute_error(activities, predictions)
        elif activity_type == 'category':
            stats['ccr'] = metrics.accuracy_score(activities, predictions)

    return Prediction(predictions, stats, descriptors_used)._asdict()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Predict a test set using the provided pickled random forest classifier object.')

    parser.add_argument('FOREST_FILE', help='the random forest model in Python pickle form (optionally gzipped)')
    parser.add_argument('TEST_X_FILE', help='the descriptor matrix for the test set')

    parser.add_argument('-o', '--output',
                        help='where to save the predictions (default: "{}")'.format(DEFAULT_OUTFILEPATH))
    parser.add_argument('-a', '--activity', nargs=2, metavar=('ACTIVITY_FILE', 'ACTIVITY_TYPE'), action=ActivityAction,
                        help=("the activity labels for the test set, and the activity type "
                              "(one of 'continuous' or 'category')"))
    parser.add_argument('-q', '--quiet', help='run quietly', action='store_true')
    args = parser.parse_args()

    logging.basicConfig(level=logging.WARNING, format='%(asctime)s %(levelname)s %(message)s')
    if args.quiet:
        pass
    else:
        logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')

    if args.FOREST_FILE.endswith('.gz'):
        logging.info('Loading gzipped pickled forest "%s"', args.FOREST_FILE)
    else:
        logging.info('Loading raw pickled forest "%s"', args.FOREST_FILE)
    forest = utils.load_model(args.FOREST_FILE)
    logging.info('Loading complete.')

    X_test = parsers.read_x_file(args.TEST_X_FILE)
    logging.info('Predicting test set "%s"', args.TEST_X_FILE)
    (prediction, tree_predictions) = (None, None)
    if args.activity is not None:
        (act_file, activity_type) = args.activity
        y_test = parsers.read_act_file(act_file, activity_type)
        prediction = predict_dataset(forest, X_test, y_test, activity_type)
        tree_predictions = [predict_dataset(tree, X_test, y_test, activity_type) for tree in forest.estimators_]
    else:
        prediction = predict_dataset(forest, X_test)

    outfilepath = args.output if args.output else DEFAULT_OUTFILEPATH
    with open(outfilepath, 'w') as outfile:
        prediction['trees'] = tree_predictions
        json.dump(prediction, outfile, cls=PandasEncoder, indent=4)
    logging.info('Predictions saved to "%s"', outfilepath)
