#!/usr/bin/python3

from sys import stdin
from string import ascii_lowercase as chars
from random import randint, choice
from operator import itemgetter
from collections import defaultdict


def FlexRadix(A, d):

    # Du må mest sannsynlig lage egne hjelpefunksjoner for denne funksjonen for å løse oppgaven.
    # Funksjonen skal returnere listen A sortert.
    # SKRIV DIN KODE HER
    for i in range(d-1, -1, -1):
       A = CountingSort(A, i)
    return A

def CountingSort(A,index):

    C = [0] * 26
    lenA = len(A)
    pos = 0
    B=[""] * lenA

    for i in range(lenA-1,-1,-1):
        if (len(A[i]) > index):
            C[(ord(A[i][index])-97)] += 1
        else:
            B[lenA-1-pos] = A[i]
        pos += 1


    for i in range(1,26):
        C[i] += C[i-1]

    for i in range(lenA-1,-1,-1):
        if len(A[i])> index:
            temp = ord(A[i][index])-97
            B[pos-1] = A[i]
            C[temp] -=1
        pos -= 1
    return B

def main():
    """
    d = int(stdin.readline())
    strings = []
    for line in stdin:
        strings.append(line.rstrip())
    """
    strings = ['a', 'kobra', 'alge', 'agg', 'kort', 'hyblen']
    d = 6
    A = FlexRadix(strings, d)
    for string in A:
        print(string)


if __name__ == "__main__":
    main()