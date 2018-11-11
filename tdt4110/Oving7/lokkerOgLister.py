__author__ = 'Marius'

#a
def separate(liste,threshold):
    liste1 = []
    liste2 = []
    for i in range(0,len(liste)):
        if liste[i] < threshold:
            liste1.append(liste[i])
        else:
            liste2.append(liste[i])
    return liste1, liste2

liste = [1,12,3,34,5,36,7,19,59,100]
verdi = int(input("Skriv inn verdien du vil splitte på:\n"))

listeMindre, listeStorre = separate(liste,verdi)

print(listeMindre,"\n", listeStorre, sep="")

#b hvorfor kan jeg endre på listemulti inni funksjonen?? uten å skrive "global listemulti" ??
def multiplication(n):
    listeMulti = [] #kommenter ut
    for i in range(1,n+1):
        tempListe = []
        for j in range(1,n+1):
            tempListe.append(i*j)
        listeMulti.append(tempListe)
    return listeMulti #kommenter ut
#listemulti = []

verdi = int(input("Skriv inn en verdi:\n"))
liste = multiplication(verdi)
for tabell in liste:
    print (tabell)
