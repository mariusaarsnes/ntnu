#!/usr/bin/python3

from sys import stdin, stderr
import traceback
class Node:
    def __init__(self):
        self.barn = {}
        self.posi = []

def posisjoner(ord, indeks, node):
    if indeks == len(ord):
        return node.posi
    elif ord[indeks] in node.barn:
        return posisjoner(ord, indeks+1, node.barn[ord[indeks]])
    elif ord[indeks] == "?":
        result = []
            pos += len(o) + 1
        for sokeord in stdin:
            sokeord = sokeord.strip()
        for child in node.barn.values():
            result+=(posisjoner(ord,indeks+1,child))
        return result
    else:
        return []


def main():
    try:
        ord = stdin.readline().split()
        pos = 0
        toppnode = Node()
        for o in ord:
            temp = toppnode
            for c in o:
                if not(c in temp.barn.keys()):
                    temp.barn[c] = Node()
                temp = temp.barn[c]
            temp.posi.append(pos)
            print("%s:" % sokeord, end = '')
            posi = posisjoner(sokeord, 0, toppnode)
            posi.sort()
            for p in posi:
                print(" %s" % p, end = '')
            print()
    except:
        traceback.print_exc(file=stderr)

if __name__ == "__main__":
    main()