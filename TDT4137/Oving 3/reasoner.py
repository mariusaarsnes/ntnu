from functools import partial
from sys import argv, exit


# Defining AND, OR and NOT functions for later use when calculating values.
def AND(x, y):
    return min(x, y)


def OR(x, y):
    return max(x, y)


def NOT(x):
    return 1.0 - x


# Functions used for finding mu-value for the different fuzzy-sets. Used when evaluating rules
def triangle(start, end, pos, clip=None):
    value = 0.0
    middle = (end + start) / 2
    if middle >= pos >= start:
        value = (pos - start) / (middle - start)
    elif end >= pos >= middle:
        value = (end - pos) / (end - middle)
    if clip is not None and value > clip:
        return clip
    return value


def grade(start, end, pos, clip=None):
    if pos >= end:
        value = 1.0
    elif pos <= start:
        value = 0.0
    else:
        value = (pos - start) / (end - start)
    if clip is not None and value > clip:
        return clip
    return value


def reverse_grade(start, end, pos, clip=None):
    value = 1.0 - grade(start, end, pos, clip=clip)
    if clip is not None and value > clip:
        return clip
    return value


def aggregate(rule_results, membership_functions, values):
    result = []
    for v in values:
        value = 0.0
        for mf, r in zip(membership_functions, rule_results):
            current = mf(v, r)
            if current > value:
                value = current
        result.append(value)
    return result


# Calculating Center of Gravity. Must have it inside a try catch for when sum(data)= 0
def cog(data, values):
    try:
        return sum(x * y for x, y in zip(data, values)) / sum(data)
    except ZeroDivisionError:
        return sum(x * y for x, y in zip(data, values)) / 1


def run():
    # If input variables are not correct, return error message
    if len(argv) != 3:
        print("Usage: python %s <distance> <delta>" % argv[0])
        exit(0)

    distance = float(argv[1])
    delta = float(argv[2])

    print("Distance: %f " % distance)
    print("Delta: %f" % delta)

    # Evaluating result of rules
    rules = [
        reverse_grade(1.0, 2.5, distance),
        AND(
            triangle(1.5, 4.5, distance),
            triangle(-1.5, 1.5, delta)
        ),
        AND(
            triangle(1.5, 4.5, distance),
            triangle(0.5, 3.5, delta)
        ),
        AND(
            triangle(3.5, 6.5, distance),
            triangle(0.5, 3.5, delta)
        ),
        AND(
            grade(7.5, 9.0, distance),
            OR(
                NOT(triangle(0.5, 3.5, delta)),
                NOT(grade(2.5, 4.0, delta))
            )
        )
    ]
    print("\nrules:")
    print(rules)

    """
        By switching out rules with tempRules you can see that with similar values,
        the discrepancy between program and answer found by hand is not as large
    tempRules = [
        0.0, 0.3, 0.4, 0.1, 0.0
    ]
    """
    agg = aggregate(
        rules,
        [
            partial(reverse_grade, -8.0, -5.0),
            partial(triangle, -7.0, -1.0),
            partial(triangle, -3.0, 3.0),
            partial(triangle, 1.0, 7.0),
            partial(grade, 5.0, 8.0)
        ],
        range(-10, 10, 1)
    )

    result = cog(agg, range(-10, 10, 1))

    print("Result set value: %f" % result)

    return


if __name__ == '__main__':
    run()
