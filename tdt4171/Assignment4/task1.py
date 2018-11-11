import matplotlib.pyplot as plt
import numpy as np
from matplotlib import cm
import random
from mpl_toolkits.mplot3d import Axes3D


def sigmoid(w, x):
    return 1 / (1 + np.exp(-x * w.T))


def sigmoid_derivative(w, x):
    return sigmoid(w, x) * (1 - sigmoid(w, x))

def l_simple(w):
    """
    simple loss function described in the assignment
    :param w: a weight pair
    :return: A loss value
    """
    x1, x2, x3 = np.matrix("1 0"), np.matrix("0 1"), np.matrix("1 1")
    return (sigmoid(w, x1) - 1) ** 2 + (sigmoid(w, x2)) ** 2 + (sigmoid(w, x3) - 1) ** 2


def l_simple_derivative(w):
    """
    Used to calculate the derivative of the loss function with respect to w
    Also the answer to 1B (finding the partial derivatives for w)
    :param w: weight pair
    :return: gradient
    """
    x1, x2, x3 = np.matrix("1 0"), np.matrix("0 1"), np.matrix("1 1")
    w1_derivative = 2 * (sigmoid(w, x1) - 1) * sigmoid_derivative(w, x1) + 2 * (
            sigmoid(w, x3) - 1) * sigmoid_derivative(w, x3)
    w2_derivative = 2 * (sigmoid(w, x2)) * sigmoid_derivative(w, x2) + 2 * (
            sigmoid(w, x3) - 1) * sigmoid_derivative(w, x3)

    return np.matrix(str(w1_derivative) + " " + str(w2_derivative))


def update_rule(w, learning_rate):
    return w - l_simple_derivative(w) * learning_rate


def gradient_decent(initial_w, learning_rate, iterations):
    w = [initial_w]
    for _ in range(iterations):
        w.append(update_rule(w[-1], learning_rate))
    return w


if __name__ == '__main__':
    # Initial setup of the figure that we are generating
    # We start with a subplot for the loss function since this is task 1a
    fig = plt.figure()
    ax = fig.add_subplot(121, projection='3d')
    ax.set_title("Loss function")
    paths = {}

    # Setting up a grid and plotting the surface
    X = np.arange(-6, 6.5, 0.5)
    Y = np.arange(-6, 6.5, 0.5)
    X, Y = np.meshgrid(X, Y)
    zs = np.array([l_simple(np.matrix(str(x) + "," + str(y))) for x, y in zip(np.ravel(X), np.ravel(Y))])
    Z = zs.reshape(X.shape)
    ax.plot_surface(X, Y, Z, cmap=cm.rainbow)
    ax.set_xlabel("w1")
    ax.set_ylabel("w2")
    ax.set_zlabel("error rate")

# 1C

ax = fig.add_subplot(1, 2, 2)
ax.set_xlabel("Epochs")
ax.set_ylabel("Error Rate")

initial_weights = np.matrix("-6 3")
print(initial_weights)
learning_rates = [0.0001, 0.01, 0.1, 1, 10, 100]
epochs = 100

for l_r in learning_rates:
    print "Gradient Decent - learning rate:", l_r
    paths[l_r] = gradient_decent(initial_weights, l_r, epochs)

    for epoch in range(0, epochs + 1, 10):
        print "Epoch:" + str(epoch).zfill(3) + \
              " Weights: " + str(paths[l_r][epoch]) + \
              " Loss: " + str(l_simple(paths[l_r][epoch]).A[0][0])
for l_r in learning_rates:
    ax.plot([l_simple(result).A[0][0] for result in paths[l_r]])
ax.legend([str(l_r) for l_r in learning_rates], title="Learning rates", bbox_to_anchor=(1, 1))

fig.tight_layout()
plt.show()
