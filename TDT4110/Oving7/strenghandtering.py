__author__ = 'Marius'
def sammenlignStr (str1, str2):
    if len(str1) != len(str2):
        return False
    else:
        for i in range(0,len(str1)):
            if str1[i] != str2[i]:
                return False
    return True

def reverser(str):
    str2 = ""
    print(str2)
    for i in range(len(str)-1,-1,-1):
        str2 +=str[i]
    return str2

#V1
def palindrom(str):
    for i in range(0,len(str)-1):
        if str[i] != str[len(str)-1-i]:
            return False
    return True

#V2
def palindromV2(str):
    str2 = reverser(str)
    if str == str2:
        return True
    else:
        return False

#V1
def inni(str,strInni):
    for i in range(0,len(str)):
        if str[i] == strInni[0]:
            #print(str[i:i+len(strInni)])
            if str[i:i+len(strInni)] == strInni:
                return i
    return False
#V2
def inniV2(str, strInni):
    if strInni in str:
        for i in range(0,len(str)-(len(strInni)-1)):
            if str[i:i+len(strInni)] == strInni:
                return i
    else:
        return False

def main():
    #a
    str1 = str(input("Skriv inn noe her:\n"))
    str2 = str(input("Skriv inn noe annet(eller ikke) her:\n"))
    lik = sammenlignStr(str1,str2)
    print(lik,"\n",sep="")

    #b
    str1 = str(input("Skiv inn noe du vil reversere:\n"))
    reversertStreng = reverser(str1)
    print(reversertStreng,"\n",sep ="")

    #c
    str1 = str(input("Skriv inn noe du vil sjekke om er et palyndrom:\n"))
    erPalindrom = palindromV2(str1)
    print(erPalindrom,"\n",sep="")

    #d
    str1 = str(input("Skriv inn noe her: \n"))
    str2 = str(input("Hva vil du sjekke om finnes i det du skrev over?\n"))
    print(inniV2(str1,str2))
main()