__author__ = 'Marius'

def readFile(filePath):
    f = open(filePath)
    list = f.readlines()
    f.close()
    list = cleanList(list)
    return list

def cleanList(list,char="\n"):
    cleanedList = []
    for element in list:
        cleanedList.append(element.split(","))
    for i in range(len(cleanedList)):
        cleanedList[i][0] = cleanedList[i][0].strip('"')
        try:
            cleanedList[i][1] = float(cleanedList[i][1].strip('"\n'))
        except:
            cleanedList[i][1] = cleanedList[i][1].strip('"\n')
    return cleanedList

def tokInnAlle(list):
    antAlle = 0
    for element in list:
        if element[1] != "Alle":
            antAlle +=1
    return antAlle

def gjsnittNTNU(list):
    ant = 0
    sumPoeng = 0
    for element in list:
        if element[0][0:4] == "NTNU" and (isinstance(element[1],int) or isinstance(element[1],float)):
            sumPoeng += element[1]
            ant +=1
    return sumPoeng / ant

def minNTNU(list):
    minst = 99999999999999
    for element in list:
        if (isinstance(element[1],int) or isinstance(element[1],float)):
            if element[1] < minst:
                minst = element[1]
    return minst



def main():
    list = readFile("poenggrenser_2011.csv")
    print(tokInnAlle(list))
    print(gjsnittNTNU(list))
    print(minNTNU(list))

main()