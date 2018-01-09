__author__ = 'Marius'

def lesFraFil(path):
    f = open(path)
    fileText = f.read()

    liste = fileText.split("\n")
    liste = strTilInt(liste)
    return liste

def strTilInt(liste):
    for i in range(0,len(liste)):
        liste[i] = int(liste[i])
    return liste

def finnForekomster(liste1,liste2,liste3):
    for i in range(0,len(liste1)):
        funnet = False
        for j in range(0,len(liste2)):
            if liste1[i] == liste2[j]:
                liste3[j] +=1
                funnet = True
                break
        if funnet == False:
            if i == 0:
                liste2[0] = liste1[i]
                liste3[0] = 1
            else:
                liste2.append(liste1[i])
                liste3.append(1)
    return liste2,liste3

#fikset på etter godkjenning av øving 6
#OBS!!! SPØR OM DET ER EN MÅTE Å HENTE UT ALLE INDEXER I EN DICT. (tallFraTil in indexer
def finnForekomsterV2(listeFraFil,listeTall = [],listeAntallForekomster ={}):
    print("startet")
    for tallFraFil in listeFraFil:
        if tallFraFil in listeTall:
            listeAntallForekomster[tallFraFil] += 1
        else:
            listeTall.append(tallFraFil)
            listeAntallForekomster[tallFraFil] = 1
    listeTall.sort()
    return listeAntallForekomster, listeTall

def main():
    strPath = "D:/OneDrive/SKOLE/NTNU/2015 Høst/ITGK/Obliger/Oblig7/numbers.txt"
    liste = lesFraFil(strPath)
    listeTall = [""]
    listeForekomst = [""]
    listeUtskriftForekomst, listeUtskriftTall  = finnForekomsterV2(liste)
    print("AntallLinjer i filen(V2):",len(liste),sep=" ")
    for element in listeUtskriftTall:
        print(element,listeUtskriftForekomst[element], sep=":")

    listeTall,listeForekomst = finnForekomster(liste,listeTall,listeForekomst)

    print("Antall linjer i filen:",len(liste), sep=" ")
    for i in range(0,len(listeTall)):
        print(listeTall[i],listeForekomst[i],sep=":")
main()