import pandas as pd
import numpy as np


def read_x_file(filepath):
    df = pd.read_csv(filepath, delim_whitespace=True, skiprows=1, header=0, index_col=[0, 1])
    df.index = [multi_index[1] for multi_index in df.index]
    df = df.astype(np.float32)
    return df


def read_act_file(filepath, activity_type):
    with open(filepath, 'r') as file:
        kwargs = {}
        try:
            [float(x) for x in file.readline().split()[1:]]
        except ValueError:
            kwargs['skiprows'] = 1
        file.seek(0)

        df = pd.read_csv(file, delim_whitespace=True, index_col=0, names=['Activity', ], squeeze=True, **kwargs)
        if activity_type == 'category':
            df = df.astype('category')
        return df
