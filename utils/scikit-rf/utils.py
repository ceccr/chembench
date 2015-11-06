import gzip
import os
import pickle
import shutil


def save_model(model, outfilepath):
    gzip_outfilepath = outfilepath + '.gz'
    with open(outfilepath, 'ab+') as outfile, gzip.open(gzip_outfilepath, 'wb') as gzip_outfile:
        pickle.dump(model, outfile)
        outfile.seek(0)
        shutil.copyfileobj(outfile, gzip_outfile)
    os.remove(outfilepath)
    return gzip_outfilepath


def load_model(filepath):
    file = None
    if filepath.endswith('.gz'):
        file = gzip.open(filepath, 'rb')
    else:
        file = open(filepath, 'rb')
    model = pickle.load(file)
    file.close()
    return model
