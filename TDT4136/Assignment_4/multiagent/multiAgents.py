# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import random

import util
from game import Agent


class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """

    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in
                  legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if
                       scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices)  # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in
                          newGhostStates]

        "*** YOUR CODE HERE ***"
        return successorGameState.getScore()


def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()


class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn='scoreEvaluationFunction', depth='2'):
        self.index = 0  # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)


class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game
        """
        "*** YOUR CODE HERE ***"

        def max_play(game_state, depth):
            """
            Pretty basic max-half og the minimax algorithm.
            :param game_state: the state of the game. used to get some
            variables used to check for values
            :param depth: current depth
            :return: if the depth is 0, we return the best move for the
            agent, else we return the best score to be used in min_play.
            """
            if game_state.isWin() or game_state.isLose():
                return game_state.getScore()

            moves = game_state.getLegalActions(0)
            best_score = float("-inf")
            best_move = moves[0]

            for move in moves:
                temp_score = min_play(
                    game_state.generateSuccessor(0, move), depth, 1)
                if temp_score > best_score:
                    best_score = temp_score
                    best_move = move
            if depth == 0:
                return best_move
            else:
                return best_score

        def min_play(game_state, depth, agent_index):
            """
            A bit more complex code. Some checks to see how deep we are in
            the tree. and also handling the fact that there is a varying amount
            (and multiple) of ghosts.
            :param game_state: the current state of the game
            :param depth: the current depth. used to know when to end the
            recursion.
            :param agent_index: says which agent type we are checking for,
            and used to figure out how many ghosts are left.
            :return: returns the best possible score (for the ghost)
            """
            if game_state.isLose() or game_state.isWin():
                return game_state.getScore()
            moves = game_state.getLegalActions(agent_index)
            best_score = float('inf')
            for move in moves:
                if agent_index == game_state.getNumAgents() - 1:
                    if depth == self.depth - 1:
                        temp_score = self.evaluationFunction(
                            game_state.generateSuccessor(agent_index, move))
                    else:
                        temp_score = max_play(game_state.generateSuccessor(
                            agent_index, move), depth + 1)
                else:
                    temp_score = min_play(game_state.generateSuccessor(
                        agent_index, move), depth, agent_index + 1)
                if temp_score < best_score:
                    best_score = temp_score
            return best_score

        return max_play(gameState, 0)
        util.raiseNotDefined()


class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"

        def max_play(game_state, depth, alpha, beta):
            """
            Max-part of the minimax algorithm, with alpha beta pruning
            :param game_state: current state of the game
            :param depth: current depth of the tree
            :param alpha: alpha value used for pruning
            :param beta: beta value used for pruning
            :return:
            """
            if game_state.isWin() or game_state.isLose():
                return game_state.getScore()
            moves = game_state.getLegalActions(0)
            best_score = float('-inf')
            best_move = moves[0]
            for move in moves:
                temp_score = min_play(game_state.generateSuccessor(0, move),
                                      depth, 1, alpha, beta)
                if temp_score > best_score:
                    best_score = temp_score
                    best_move = move
                alpha = max(alpha, best_score)
                if best_score > beta:
                    return best_score
            if depth == 0:
                return best_move
            else:
                return best_score

        def min_play(game_state, depth, agent_index, alpha, beta):
            """
            Minimax with alpha beta pruning min-part. the same as simple
            minimax, just checking if best_score is less than beta aswell
            :param game_state: current state og the game
            :param depth: depth of the tree
            :param agent_index: the current agent we are checking for
            :param alpha: alpha value used for pruning
            :param beta: beta value used for pruning
            :return:
            """
            if game_state.isWin() or game_state.isLose():
                return game_state.getScore()
            moves = game_state.getLegalActions(agent_index)
            best_score = float('inf')
            for move in moves:
                if agent_index == game_state.getNumAgents() - 1:
                    if depth == self.depth - 1:
                        temp_score = self.evaluationFunction(
                            game_state.generateSuccessor(agent_index, move))
                    else:
                        temp_score = max_play(game_state.generateSuccessor(
                            agent_index, move), depth + 1, alpha, beta)
                else:
                    temp_score = min_play(game_state.generateSuccessor(
                        agent_index, move), depth, agent_index + 1, alpha,
                        beta)
                if temp_score < best_score:
                    best_score = temp_score
                beta = min(beta, best_score)
                if best_score < alpha:
                    return best_score
            return best_score

        return max_play(gameState, 0, float('-inf'), float('inf'))
        util.raiseNotDefined()


class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        util.raiseNotDefined()


def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    util.raiseNotDefined()


# Abbreviation
better = betterEvaluationFunction
