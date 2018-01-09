__author__ = 'Marius'
import math
def ducci(list):
    lik = False

    while not lik:
        tempList = []
        sum = 0

        for i in list:
            tempList.append(i)

        for i in range(0,len(list)):
            if i == len(list)-1:
                list[i] = math.fabs(list[i]-tempList[0])
            else:
                list[i] = math.fabs(list[i]-list[i+1])
            sum+= list[i]
        print(tempList, "\n", list, "\n", sep="")

        if sum == 0:
            lik = True

ducci([10, 8, 12, 15, 5, 7, 43, 21])