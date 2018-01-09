from sys import stdin

def mergeSort(numbers):
    length = len(numbers)
    if length >= 2:
        mid = length // 2
        firstHalf = mergeSort(numbers[:mid])
        secondHalf = mergeSort(numbers[mid:])
        return merge(firstHalf,secondHalf)
    else:
        return numbers

def merge(firstHalf, secondHalf):
    firstLen = len(firstHalf)
    secondLen= len(secondHalf)
    result = []
    i = 0
    j = 0
    while (i < firstLen and j < secondLen):
        firstElement = firstHalf[i]
        secondElement = secondHalf[j]
        if firstElement <= secondElement:
            result.append(firstElement)
            i += 1
        else:
            result.append(secondElement)
            j += 1
    if i == firstLen:
        result += secondHalf[j:]
    else:
        result += firstHalf[i:]
    return result



def find(list, minValue, maxValue):
    minIndex = binarySearch(list,minValue)
    maxIndex = binarySearch(list,maxValue)


    if list[minIndex] > minValue and minIndex > 0:
        minIndex -= 1
    if list[maxIndex] < maxValue and maxIndex < len(list) -1:
        maxIndex += 1
    return [list[minIndex],list[maxIndex]]



def binarySearch(list, value):
    lastIndex = len(list) -1
    startPos = 0
    while (startPos <= lastIndex):
        midIndex = (startPos + lastIndex) // 2
        if list[midIndex] == value:
            return midIndex
        elif list[midIndex] > value:
            lastIndex = midIndex - 1
        else:
            startPos = midIndex + 1
    return midIndex

def main():
    numbers = mergeSort(list(map(int, stdin.readline().split())))
    for line in stdin:
        temp = line.split()
        values = find(numbers, int(temp[0]), int(temp[1]))
        print(str(values[0]) + " " + str(values[1]))
if __name__ == "__main__":
    main()