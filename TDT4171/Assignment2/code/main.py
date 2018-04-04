import numpy

from assignment2 import algorithm


def task_b(observations):
    """
    Function that tests Task B
    :param observations: the set of observations that should be fed into the
    Forward algorithm
    :return: the normalized vector
    """

    fb_alg = algorithm.ForwardBackward()
    rain_prob = numpy.matrix('0.5;0.5')
    result = [rain_prob]

    for umbrella in observations:
        rain_prob = fb_alg.forward(rain_prob, umbrella)
        result.append(fb_alg.normalize(rain_prob))
    return result


def task_c(observations):
    """
    Function that tests Task C
    :param observations: list of observations for each step
    :return: the normalized vector
    """
    rain_prob = numpy.matrix('0.5;0.5')
    fb_alg = algorithm.ForwardBackward()
    return fb_alg.forward_backward(observations, rain_prob)


def main():
    """
    Main program where all tasks are run
    :return:
    """
    observations = [True, True, False, True, True]

    # Task B: Run forward algorithm on 2 steps
    temp = task_b(observations[:2])

    for step, t in enumerate(temp):
        print(step, t[0], t[1])

    # Task B: Run forward algorithm on all 5 steps
    temp = task_b(observations)
    for step, t in enumerate(temp):
        print(step, t[0], t[1])


    # Task C: Run forwardbackward algorithm for 2 steps
    temp = task_c(observations[:2])
    print("forward_backward result")
    for step, t in enumerate(temp):
        print(step+1, t[0], t[1])

    # Task C: Run forwardbackward algorithm for all steps
    temp = task_c(observations)
    print("forward_backward result")
    for step, t in enumerate(temp):
        print(step+1, t[0], t[1])


if __name__ == '__main__':
    main()
