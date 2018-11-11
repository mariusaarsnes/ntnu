import decisionTree


def get_data(path):
    """
    Simple function to read the content of the provided file.
    Used for getting both test and training.txt data
    :param path: filepath to the file that should be read
    :return:
    """
    with open(path) as f:
        content = f.readlines()

    # Make a list of 0 and 1 to make it easier later when using the data.
    # Makes it easier to find out if the examples are classifiable and cound
    # of pos and neg values
    return [list(map(lambda x: int(x) - 1, line.strip().split('\t'))) for
            line in content]


def main():
    training_data = get_data("data/training.txt")
    testing_data = get_data("data/test.txt")

    print("Running Training with random importance and no reuse of splitting "
          "attributes")
    dt = decisionTree.DecisionTreeLearning(training_data, testing_data, True)
    for _ in range(5):
        root = dt.train()
        print('Random score: %f' % (dt.test()))
    print(root)

    print("Running Training with information gain importance and no reuse of "
          "splitting attributes")
    dt = decisionTree.DecisionTreeLearning(training_data, testing_data, False)
    for _ in range(5):
        root = dt.train()
        print('Information gain score:', dt.test())
    # Used to print out string in format compatible with http://mshang.ca/syntree/
    print(root)

    print("Running Training with random importance with reuse of splitting "
          "attributes")
    dt = decisionTree.DecisionTreeLearning(training_data, testing_data, True,
                                           True)
    for _ in range(5):
        root = dt.train()
        print('Random score: %f' % (dt.test()))
    print(root)

    print("Running Training with information gain importance with reuse of "
          "splitting attributes")
    dt = decisionTree.DecisionTreeLearning(training_data, testing_data,
                                           False, True)
    for _ in range(5):
        root = dt.train()
        print('Information gain score:', dt.test())
    print(root)


main()
