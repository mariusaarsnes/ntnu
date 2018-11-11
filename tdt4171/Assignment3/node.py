class Node:
    def __init__(self, value):
        self.name = value
        self.children = {}

    def getRep(self):
        return str(self.name)

    def __str__(self):
        """
        String representation of the tree, used to generate a tree at http://mshang.ca/syntree/
        :return:
        """

        if self.children:
            return '[%s ' % self.name + '%s]' % ''.join(str(self.children[key])
                                                        for key in
                                                        self.children.keys())
        else:
            return '[%s]' % self.name

    def __repr__(self):
        """
        representation of the node, used with other_name method
        :return: string
        """

        if self.children:
            return '%s(atr)' % self.name
        else:
            return '%s(leaf)' % self.name

    def other_name(self, level=0):
        """
        An attempt to make a readable printout of the constructed tree
        :param level:
        :return:
        """
        print('--' * level + repr(self), end="")
        if level == 0:
            print("(root)")
        else:
            print()
        for key in self.children:
            self.children[key].other_name(level + 1)
