__author__ = 'Marius'
import random

def makeList(length, a , b):
    list = []
    while len(list) < length:
        list.append(random.randint(a,b))
    return list


def compareTwoLists():
    # Lager to lister som er mellom 1 og 10 lang med tall mellom 1 og 10
    list1 = makeList(random.randint(1, 10), 0, 9)
    list2 = makeList(random.randint(1, 10), 0, 9)
    print("list1:", list1, "\nList2:", list2, sep=" ")
    numbersInBoth = []
    for number in list1:
        if number in list2 and not number in numbersInBoth:
            numbersInBoth.append(number)
    return numbersInBoth

#random antall
def compareLists():
    listOfLists = []
    numbersInAll = []
    for i in range(2,random.randint(4,10)):
        listOfLists.append(makeList(random.randint(1,10), 0, 9))
    for number in listOfLists[0]:
        innAll = True
        for i in range(1, len(listOfLists)):
            if number not in listOfLists[i]:
                innAll = False
                break
        if innAll and not number in numbersInAll:
            numbersInAll.append(number)
    return listOfLists,  numbersInAll

def evenNumbersRow():
    list = makeList(random.randint(2,10),0,20)
    pLength = 0
    length = 0
    start = "Det er ingen partall"
    for i in range(0,len(list)):
        if list[i] % 2 == 0:
            length += 1
            pLength = length
            if list[i-1] % 2 != 0:
                start = i
        else:
            length = 0
    print(list)
    print(pLength)
    print(start)

def main():
    print("liste: ", makeList(4,1,10),"\n", sep="")

    print("Liste med tall som fins i begge listene: ", compareTwoLists(),"\n", sep="")

    listOfLists, numbersInAll = compareLists()
    for i in range(0,len(listOfLists)):
        print(listOfLists[i])
    print("Inneholder alle: ", numbersInAll,"\n", sep="")

    evenNumbersRow()

main()