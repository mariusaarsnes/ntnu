#!/usr/bin/python3

from sys import stdin


class Node:
    barn = None 
    ratatosk = None
    nesteBarn = None  # bare til bruk i DFS

    def __init__(self):
        self.barn = []
        self.ratatosk = False
        self.nesteBarn = 0


def dfs(rot):
    # SKRIV DIN KODE HER
    stack = []
    stack.append(rot)
    stackLen = 0
    hoide = 0
    while True:
        if (stack[stackLen].ratatosk):
            return hoide
        if (stack[stackLen].barn == []):
            stack.pop()
            stackLen -=1
            if stack[stackLen].nesteBarn == 1:
                hoide -= 1
        else:
            hoide += 1
            temp = 0
            for element in stack[stackLen].barn:
                stack.append(element)
                temp += 1
            stack[stackLen].barn = []
            stack[stackLen].nesteBarn = 1
            stackLen += temp


def bfs(rot):
    # SKRIV DIN KODE HER
    queue = []
    queue.append(rot)
    head = 0
    hoide = 0
    nextLvl1 = 0
    nextLvl2 = 0
    while True:
        if queue[head].ratatosk:
            return hoide
        queue += queue[head].barn
        if head == nextLvl2:
            hoide +=1
            nextLvl2 = len(queue)-1
        head += 1





funksjon = stdin.readline().strip()
antall_noder = int(stdin.readline())

noder = []
for i in range(antall_noder):
    noder.append(Node())
start_node = noder[int(stdin.readline())]
ratatosk_node = noder[int(stdin.readline())]
ratatosk_node.ratatosk = True
for linje in stdin:
    tall = linje.split()
    temp_node = noder[int(tall.pop(0))]
    for barn_nr in tall:
        temp_node.barn.append(noder[int(barn_nr)])
if funksjon == 'dfs':
    print(dfs(start_node))
elif funksjon == 'bfs':
    print(bfs(start_node))
elif funksjon == 'velg':
    # SKRIV DIN KODE HER
    print(bfs(start_node))
