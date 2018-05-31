import random
from time import time
import multiprocessing as mp

import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
from matplotlib import cm


def logistic_z(z):
    return 1.0 / (1.0 + np.exp(-z))


def logistic_wx(w, x):
    return logistic_z(np.inner(w, x))


def classify(w, x):
    x = np.hstack(([1], x))
    return 0 if (logistic_wx(w, x) < 0.5) else 1


# x_train = [number_of_samples,number_of_features] = number_of_samples x \in R^number_of_features
def stochast_train_w(x_train, y_train, learn_rate=0.1, niter=1000):
    x_train = np.hstack((np.array([1] * x_train.shape[0]).reshape(x_train.shape[0], 1), x_train))
    dim = x_train.shape[1]
    num_n = x_train.shape[0]
    w = np.random.rand(dim)
    index_lst = []
    for _ in xrange(niter):
        if len(index_lst) == 0:
            index_lst = random.sample(xrange(num_n), k=num_n)
        xy_index = index_lst.pop()
        x = x_train[xy_index, :]
        y = y_train[xy_index]
        for i in xrange(dim):
            update_grad = x[i] * (logistic_wx(w, x) - y) * logistic_wx(w, x) * (1 - logistic_wx(w, x))
            w[i] = w[i] - learn_rate * update_grad  # update weights
    return w


def batch_train_w(x_train, y_train, learn_rate=0.1, niter=1000):
    x_train = np.hstack((np.array([1] * x_train.shape[0]).reshape(x_train.shape[0], 1), x_train))
    dim = x_train.shape[1]
    num_n = x_train.shape[0]
    w = np.random.rand(dim)
    # index_lst = []
    for _ in xrange(niter):
        for i in xrange(dim):
            update_grad = 0.0
            for n in xrange(num_n):
                x = x_train[n]
                y = y_train[n]
                update_grad += (logistic_wx(w, x) - y) * x[i] * logistic_wx(w, x) * (1 - logistic_wx(w, x))
            w[i] = w[i] - learn_rate * update_grad / num_n
    return w


def plot_points(x_list, y_list, color, diagram_title):
    """
    Used to make diagrams. Not in use since the assignment specifies that the data needs to be in the same grid
    :param x_list: list of x values
    :param y_list: list of y values
    :param color: color of the data points
    :param diagram_title: title of the diagram
    :return:
    """
    data_plotter = pd.DataFrame(np.hstack((x_list, y_list.reshape(x_list.shape[0], 1))), columns=['x', 'y', 'lab'])
    data_plotter.plot(kind='scatter', x='x', y='y', c='lab', cmap=color, edgecolors='black', title=diagram_title)
    return


def train_and_plot(x_train, y_train, x_test, y_test, training_method, learn_rate=0.1, niter=100):
    # plt.figure()
    # train data

    data = pd.DataFrame(np.hstack((x_train, y_train.reshape(x_train.shape[0], 1))), columns=['x', 'y', 'lab'])
    ax = data.plot(kind='scatter', x='x', y='y', c='lab', cmap=cm.copper, edgecolors='black')

    # train weights
    time_to_train = time()
    w = training_method(x_train, y_train, learn_rate, niter)
    time_to_train = time() - time_to_train
    error = []
    y_est = []
    for i in xrange(len(y_test)):
        error.append(np.abs(classify(w, x_test[i]) - y_test[i]))
        y_est.append(classify(w, x_test[i]))

    y_est = np.array(y_est)
    data_test = pd.DataFrame(np.hstack((x_test, y_est.reshape(x_test.shape[0], 1))), columns=['x', 'y', 'lab'])
    data_test.plot(kind='scatter', x='x', y='y', c='lab', cmap=cm.coolwarm, edgecolors='black', ax=ax)
    plt.show()

    mean_error = np.mean(error)
    print "error=", mean_error
    return w, time_to_train, mean_error


def get_data(pos):
    training_files = [
        "data/data_big_nonsep_train.csv",
        "data/data_big_separable_train.csv",
        "data/data_small_nonsep_train.csv",
        "data/data_small_separable_train.csv"
    ]

    testing_files = [
        "data/data_big_nonsep_test.csv",
        "data/data_big_separable_test.csv",
        "data/data_small_nonsep_test.csv",
        "data/data_small_separable_test.csv"
    ]

    x_train = np.loadtxt(training_files[pos], delimiter="\t", usecols=(0, 1))
    y_train = np.loadtxt(training_files[pos], delimiter="\t", usecols=2)

    x_test = np.loadtxt(testing_files[pos], delimiter="\t", usecols=(0, 1))
    y_test = np.loadtxt(testing_files[pos], delimiter="\t", usecols=2)

    return x_train, y_train, x_test, y_test


if __name__ == '__main__':
    iterations = [10, 20, 50, 100, 200, 500]
    data_sets = [1]  # [0,1,2,3]
    num_training = 1 # 10
    training_types = [stochast_train_w]  # , batch_train_w]
    avg_errs, avg_times = [[] for _ in range(4)], [[] for _ in range(4)]
    for training_type in training_types:
        print "---------- Start Training Using", training_type.func_name, "----------"
        for data_set in data_sets:
            print "------ Running for dataset", data_set, "------"
            x_train, y_train, x_test, y_test = get_data(data_set)
            for i in range(len(iterations)):
                print "------ Iterations:", iterations[i], "------"
                avg_times[data_set].append([])
                avg_errs[data_set].append([])
                for j in range(num_training):
                    print "------ Training round:", j + 1, "------"
                    w, temp_time, temp_err = train_and_plot(x_train,
                                                            y_train,
                                                            x_test,
                                                            y_test,
                                                            training_type,
                                                            niter=iterations[i])

                    avg_times[data_set][i].append(temp_time)
                    avg_errs[data_set][i].append(temp_err)
        print "------ Final Results: ------"
        for data_set in data_sets:
            for i in range(len(iterations)):
                print "Data: ", data_set, " Epochs: ", iterations[i]
                print "Avg. times: ", np.mean(avg_times[data_set][i])
                print "Avg. Errors: ", np.mean(avg_errs[data_set][i])
