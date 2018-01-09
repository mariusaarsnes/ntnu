#!/usr/bin/python3

from sys import stdin


def FlexRadix(A, d):
    dict={x:[] for x in range(d+1)}
    for element in A:
        dict[len(element)].append(element)
    for i in range(d,0,-1):
        dict[i-1] += ComparisonSort(dict[i], 26, i)
    return dict[0]

def ComparisonSort(A,k, d):
    lenA = len(A)
    C= [0] * k
    B = [""] * lenA
    for i in range(lenA):
        C[ord(A[i][d-1])-97] +=1
    for i in range(1,k):
        C[i] += C[i-1]

    for i in range(lenA-1, -1,-1):
        B[C[ord(A[i][d-1])-97]-1] = A[i]
        C[ord(A[i][d-1])-97] -= 1
    return B

def main():
    d = int(stdin.readline())
    strings = []
    for line in stdin:
        strings.append(line.rstrip())
    A = FlexRadix(strings, d)
    for string in A:
        print(string)


if __name__ == "__main__":
    main()