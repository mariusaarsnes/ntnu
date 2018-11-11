#!/usr/bin/python3


from sys import stdin
from itertools import repeat

def merge(decks):
    length = len(decks)
    if length >= 2:
        mid = length//2
        firstHalf = merge(decks[:mid])
        secondHalf = merge(decks[mid:])
        return sortFunk(firstHalf, secondHalf)
    else:
        return decks[0]

def sortFunk(list1, list2):
    tempList = []
    i= 0
    j = 0
    len1 = len(list1)
    len2 = len(list2)
    while (len1 > i and len2 > j):
        if list1[i][0] > list2[j][0]:
            tempList.append(list2[j])
            j += 1
        else:
            tempList.append(list1[i])
            i += 1
    if len1 == i:
        tempList += list2[j:]
    else:
        tempList += list1[i:]
    return tempList


def main():
    # Read input.
    decks = []
    for line in stdin:
        (index, csv) = line.strip().split(':')
        decks.append(list(zip(map(int, csv.split(',')), repeat(index))))
    # Merge the decks and print result.
    print("".join(i[1] for i in merge(decks)))


if __name__ == "__main__":
    main()