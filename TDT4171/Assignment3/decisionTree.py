import math
import random

from node import Node


class DecisionTreeLearning:
    def __init__(self, training_set, testing_set, random=False,
                 allow_reuse_atr=False):
        """
        Simple constructor used to instantiate the decision tree learner
        with variables.
        :param training_set: examples used for training.txt
        :param testing_set: examples used for testing
        :param random: decide if you should pick random attributes or not

        """
        self.training_set = training_set
        self.testing_set = testing_set
        self.attributes = list(range(7))
        self.root = None
        self.random = random

        # used to specify is attributes are allowed to
        # appear multple times or not
        self.allow_reuse_atr = allow_reuse_atr

    def test(self):
        """
        Trying to classify the test set using the constructed decision tree
        """
        count = 0
        for example in self.testing_set:
            if example[-1] == self.classify(example):
                count += 1
        return count

    def classify(self, example):
        """
        Choosing which action to take
        :param example:
        :return:
        """
        root = self.root
        while root.children:
            root = root.children[example[root.name]]
        return root.name

    def train(self):
        """
        Basically just a wrapper method around the actual functionality of
        _deicision_tree_learning where the actual functionality.
        if the functionality is implemented through simple functions and no
        class, this is not necessary, but it looks nicer
        :return: root of the decision tree
        """
        attributes = list(self.attributes)
        self.root = self._decision_tree_learning(self.training_set,
                                                 attributes, list())

        return self.root

    def _decision_tree_learning(self, examples, attributes, parent_examples):
        """
        The decisoin tree learning algorithm from the book
        :param examples: list of current examples
        :param attributes: available attributes to split on
        :param parent_examples: the examples used to split the parent node
        :return: the root node of a subtree
        """

        if not examples:
            return Node(self.plurality_value(parent_examples))
        elif self.is_classifiable(examples):
            return Node(examples[0][-1])
        elif not attributes:
            return Node(self.plurality_value(examples))
        else:
            # Normally we would just use the importance function, but since
            # the assignment asks us for the possibility of choosing
            # randomly, we need to have that here
            if self.random:
                a = random.choice(attributes)
            else:
                a = self.importance(examples, attributes)
            tree = Node(a)
            attributes.remove(a)

            # Loop through all possible values of a. in this case it is
            # either 0 or 1, since these are the two values each attribute
            # can have
            for i in range(2):
                exs = [example for example in examples if example[a] == i]

                # We check if we allow for reuse of the same splitting
                # attribute multiple times. If we do, we send in a copy of
                # the attribute list, if not we send the actual attribute list
                if self.allow_reuse_atr:
                    subtree = self._decision_tree_learning(exs,
                                                           list(attributes),
                                                           examples)
                else:
                    subtree = self._decision_tree_learning(exs,
                                                           attributes,
                                                           examples)

                tree.children[i] = subtree
        return tree

    def plurality_value(self, examples):
        """
        :param examples:
        :return: the result that appears most often in the examples
        """
        p, n = self._enumPosAndNeg(examples, -1)

        if p > n:
            return 1
        elif p < n:
            return 0
        return random.randint(0, 1)

    def is_classifiable(self, examples):
        """
        checks if we can classify the examples, split them in two
        :param examples:
        :return:
        """
        p, _ = self._enumPosAndNeg(examples, -1)

        if p == 0 or p == len(examples):
            return True
        return False

    def importance(self, examples, attributes):
        """
        Used to find the information gain for an attribute
        :param examples:
        :param attributes:
        :return:
        """
        b_goal = self._h(examples, -1)
        temp = {attribute: b_goal - self._remainder(attribute, examples) for
                attribute in attributes}
        max_val = -math.inf
        a = 0
        for key, value in temp.items():
            if value > max_val:
                a = key
                max_val = temp[key]
        return a

    def _enumPosAndNeg(self, examples, pos):
        """
        Used for counting number of positive and negative examples
        :param examples: list of examples to be enumerated
        :param pos: the position of the value to be checked, usually the last i.e. the result
        :return:
        """
        p = 0
        for example in examples:
            p += example[pos]
        n = len(examples) - p
        return p, n

    def _h(self, examples, pos):
        p, n = self._enumPosAndNeg(examples, pos)
        return self._b(p / (p + n))

    def _b(self, q):
        """
        Calculate entropy for the given q (same function as in the book)
        just need to handle the cases where q is either 1 or 0, since this
        messes with the use of log
        :param q:
        :return:
        """
        if q == 0 or q == 1:
            return 0.0
        return -(q * math.log2(q) + (1.0 - q) * math.log2(1.0 - q))

    def _remainder(self, atr, examples):
        """
        Used to calculate the remainder for a specific attribute
        :param atr: the attribute to be calculated for
        :param examples: list of the current examples
        :return: calculated remainder value for attribute
        """
        res = 0
        p, n = self._enumPosAndNeg(examples, -1)

        for i in range(2):
            temp_examples = [example for example in examples if example[atr]
                             == i]
            p_k, n_k = self._enumPosAndNeg(temp_examples, -1)
            res += (p_k + n_k) / (p + n) * self._h(temp_examples, -1)
        return res
