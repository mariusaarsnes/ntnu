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
        cleanedList.append(int(element.strip(char)))
    return cleanedList


def number_of_lines(fileName):
    list = readFile(fileName)
    return len(list)

def number_frequency(fileName):
    list = readFile(fileName)
    dict = {}
    for element in list:
        if element not in dict:
            dict[element] = 0
        dict[element] += 1

    return dict

def main():
    print("Antall linjer i filen er: ", number_of_lines("numbers.txt"), sep="")
    dict = number_frequency("numbers.txt")
    for key in dict:
        print(key,": ", dict[key])

main()