__author__ = 'Marius'

def pascalsTrekant (n):
    list = []
    tempList = []
    for i in range(0,n):
        tempList = []
        for j in range(0,i+1):
            try:
                print(i,": ", list[i-1][j-1],"+",list[i-1][j])
                tempList.append(list[i-1][j-1]+list[i-1][j])
            except:
                tempList.append(1)
        list.append(tempList)

    print (list)


pascalsTrekant(3)