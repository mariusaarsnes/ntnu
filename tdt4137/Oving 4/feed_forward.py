from pybrain.datasets import SupervisedDataSet
from pybrain.structure import TanhLayer
from pybrain.supervised.trainers import BackpropTrainer
from pybrain.tools.shortcuts import buildNetwork


class FeedForward():
    def __init__(self, hidden_nodes_count):
        self.data_set = self.generate_data_set()

        self.network = buildNetwork(1, hidden_nodes_count, 1, bias=True,
                                    hiddenclass=TanhLayer)

        self.trainer = self.train()

    def generate_data_set(self):
        """
        Generates the dataset used in the assignment. 1->1, 2->2, and so on...
        :return:
        """
        data_set = SupervisedDataSet(1, 1)

        # We don't really care about the outer loop-variable, therefore we
        # just use _
        for i in range(9):
            data_set.addSample(i, i)

        return data_set

    def train(self):
        """
        Used to train the network, using backpropagation.
        :return:
        """
        temp_trainer = BackpropTrainer(self.network, self.data_set)

        temp_trainer.trainUntilConvergence(verbose=False,
                                           validationProportion=0.15,
                                           maxEpochs=1000, continueEpochs=10)
        return temp_trainer

    def run(self, input_value):
        return self.network.activate([input_value])


def log(text):
    """
    Used to print to console, mostly used for debugging purposes but also to
    get a prettier output in console.
    :param text: text to be printed
    :return:
    """
    print("-----------------------", text, "-----------------------")


def main():
    values_inside_range = [1, 2, 3, 4, 5, 6, 7, 8]
    values_outside_range = [-5, 9, 0, 5.5, 65]
    for hidden_layer_nodes in range(9):
        log("Starting run with " + hidden_layer_nodes + " hidden nodes")
        network = FeedForward(hidden_layer_nodes)

        log("running with values inside range")
        for value in values_inside_range:
            log(str(value) + ": " + str(network.run(value)))

        log("running with values outside range")
        for value in values_outside_range:
            log(str(value) + ": " + str(network.run(value)))

        log("End")

if __name__ == "__main__":
    main()
