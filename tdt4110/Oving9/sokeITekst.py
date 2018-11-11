__author__ = 'Marius'


def readFile(filePath):
    f = open(filePath)
    text = f.read()
    f.close()
    return text

def fjernTegn(text):
    text = text.lower()
    text2 = ""
    for i in range(len(text)):
        if (ord(text[i]) >= 97 and ord(text[i]) <= 122) or  ord(text[i]) == 32:
            text2 += text[i]
    return text2

def forekomstOrd(fileName):
    text = fjernTegn(readFile(fileName))
    list = text.split(" ")
    dict = {}
    for element in list:
        if not element in dict:
            dict[element] = 0
        dict[element] += 1
    return dict

def main():
    dict = forekomstOrd("tekst.txt")

    for key in dict:
        print(key, ": ", dict[key], sep="")
main()