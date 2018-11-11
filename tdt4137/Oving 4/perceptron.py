import random


def log(input):
    print("---------------------", input, "---------------------")


class Perceptron(object):
    def __init__(self, t, desired_output_function, n=2, lrate=0.1):
        # Variables used later fro the assignment
        self.x = None
        self.y = None
        self.y_d = None

        # The number of incoming values
        self.number_of_inputs = n
        # The rate at which the perceptron will learn
        self.learning_rate = lrate
        self.threshold = t
        self.desired_output_function = desired_output_function

        # w is an array or randomly generated weights.
        self.w = self.generate_matrix(self.number_of_inputs, 1,
                                      self.generate_random_weight)

    def generate_random_weight(self):
        """
        This function is used to generate random weights used when
        instantiating the perceptron
        :return: random value between -0.5 and 0.5
        """
        return random.uniform(-0.5, 0.5)

    def generate_matrix(self, n, mode, value_generator):
        """
        Generates a matrix used as weights for the perceptron
        :param n: Number of input sources
        :param mode: Used to decide the dimensions of the matrix
        :param value_generator: Used to generate values for the weight-matrix
        :return: A n*m-dimensional matrix containing random values
        """
        matrix = []
        for i in range(n):
            if mode is 1:
                matrix.append(value_generator())
            else:
                row = []
                for j in range(mode):
                    row.append(value_generator())
                matrix.append(row)
        return matrix

    def get_error(self):
        """
        :return: The error in the actual output, from the desired output
        """
        return self.y_d - self.y

    def get_delta(self, i):
        """

        :param i:
        :return:
        """
        return self.learning_rate * self.x[i] * self.get_error()

    def calculate_output(self):
        """
        Function used to calculate the actual output
        First we calculate the sum of the input-value*weight for each input value
        :return: If sum is larger of equal to 0, we return 1-threshold.
        else, we return the nagation of the threshold.
        """
        temp_sum = sum(
            self.x[i] * self.w[i] for i in range(self.number_of_inputs))
        temp_val = 1 if temp_sum - self.threshold >= 0 else 0
        return temp_val

    def set_initial_values(self, x):
        """
        Sets the input value and calculates the output-value and sets desired
        output-value
        :param x: input values
        """
        self.x = x

        self.y = self.y = self.calculate_output()
        self.y_d = self.desired_output_function(*self.x)

    def train(self, input_values_list):
        for input_values in input_values_list:
            self.set_initial_values(input_values)

            for i in range(self.number_of_inputs):
                self.w[i] += self.get_delta(i)
            if abs(self.get_error()) > 0:
                print("Weights: ", str(self.w))
                self.train(input_values_list)


if __name__ == "__main__":
    for desired_output_function in [['AND', lambda x1, x2: x1 and x2],
                                    ['OR', lambda x1, x2: x1 or x2]]:
        input_values_list = [[0, 0], [0, 1], [1, 0], [1, 1]]
        perceptron = Perceptron(random.uniform(-0.5, 0.5),
                                desired_output_function[1],
                                len(input_values_list[0]))
        log("")
        log(desired_output_function[0])
        log("Threshold: " + str(perceptron.threshold))
        log("Initial Weights: " + str(perceptron.w))

        try:
            perceptron.train(input_values_list)
        except RuntimeError:

            log("Infinite loop: No converge achieved. Combination of "
                "threshold and initial weights leads to "
                "deadlock.")
        finally:
            print("Result weights: ", ', '.join(map(str, perceptron.w)))

            for input_values in input_values_list:
                perceptron.set_initial_values(input_values)

                print("Input", input_values, ". Result: ",
                      str(input_values), perceptron.y)
