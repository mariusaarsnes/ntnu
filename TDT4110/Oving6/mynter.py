__author__ = 'Marius'

#a

myntListe =[20,20,10,5,1,20,1,1,1,5]
antMyntDictMal = {20: 0, 10: 0, 5: 0, 1:0}
def countCoins(myntListe,antMyntListe={20:0,10:0,5:0,1:0}):
    for mynt in myntListe:
        antMyntListe[mynt] += 1
    return antMyntListe

print(countCoins(myntListe))

#b
def antMyntSomTrengs(verdi, mynt, ant):
    verdi -= mynt
    ant +=1
    if verdi >= mynt:
        verdi, ant = antMyntSomTrengs(verdi,mynt,ant)
        return verdi,ant
    else:
        return verdi, ant

def numCoins(liste):
    for verdi in liste:
        antMyntDictMal = {20: 0, 10: 0, 5: 0, 1:0}
        print(verdi)
        if verdi >= 20:
            verdi,ant = antMyntSomTrengs(verdi,20,0)
            antMyntDictMal[20] = ant
        if verdi >= 10:
            verdi, ant = antMyntSomTrengs(verdi,10,0)
            antMyntDictMal[10] = ant
        if verdi >= 5:
            verdi, ant = antMyntSomTrengs(verdi, 5,0)
            antMyntDictMal[5] = ant
        if verdi >= 1:
            verdi, ant = antMyntSomTrengs(verdi, 1,0)
            antMyntDictMal[1] = ant
        print("Fordeling av mynter:",antMyntDictMal,"\n")




liste = [12,23,34,45,56,67,78,89,90,98,87,65,54,43,21]
numCoins(liste)