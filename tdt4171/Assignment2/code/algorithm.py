import numpy


class ForwardBackward():
    def __init__(self):
        """
        The constructor sets the transition model and the observation model
        """

        print("New instance of ForwardBackward class is initializing")

        # Transition model as described in the book
        self.t_model = numpy.matrix('0.7 0.3; 0.3 0.7')

        # Observation model (sensor model)
        self.o_model = {
            True: numpy.matrix('0.9 0;0 0.2'),
            False: numpy.matrix('0.1 0 ; 0 0.8')
        }

    def forward(self, f, e):
        """
        The "forward part" of the algorithm.
        :param f: previous state
        :param e: If the umbrella was observed (True/False)
        :return: forward message
        """

        # dot product of the observation model for the given observation
        # (umbrella or no umbrella), the transition model (which is the same
        # for each state, and the previous state (forward message from
        # previous call to froward)
        forward = self.o_model[e] * self.t_model * f

        return forward

    def backward(self, f, e):
        """
        The "backward part" of the algorithm
        :param f: current state
        :param e: True or False depending on if the umbrella was observed.
        :return: the backward message
        """
        # dot product of the observation model for the given observation
        # (umbrella or no umbrella), the transition, and the current state
        backward = self.t_model * self.o_model[e] * f

        return backward

    def forward_backward(self, ev, prior):
        """
        Smoothing implemented as described in the Russel & Norgiv book.
        :param ev: a vector of evidence values for steps  1,...,t
                    (Umbrella = True or Umbrella = False)
        :param prior: an initial probability matrix {1, 1}
        :return: a vector of smoothed estimates for steps 1,..., t
        """

        # get the number of observations for later use
        obs = len(ev)
        fv = []

        fv.append(prior)
        sv = []

        for i in range(obs):
            fv.append(self.normalize(self.forward(fv[i], ev[i])))

        for step, t in enumerate(fv):
            t = self.normalize(t)
            print(step, t[0], t[1])

        # As described in the book, we begin with probability (1,1) opposed
        # to (0.5,0.5) which is used with forward
        b = numpy.matrix('1;1')

        print("Backward unnormalized - result")
        print(5, b[0], b[1])
        for i in range(obs - 1, -1, -1):
            sv.append(self.normalize(numpy.multiply(fv[i + 1], b)))
            temp = self.backward(b, ev[i])
            print(i, temp[0], temp[1])
            b = temp

        return sv

    def normalize(self, vector):
        """
        Used to normalize the vector so that it sums up to 1.
        :param vector: the vector that should be normalized
        :return: Normalized vector
        """

        return vector / sum(vector)
