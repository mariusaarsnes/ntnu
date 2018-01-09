__author__ = 'Marius'
import math

def lagVektor(x,y,z):
    return [x,y,z]

def printVec():
    vec = lagVektor(verdi1,verdi2,verdi3)
    print("Vektor = ",vec, sep="")
    return vec
#vec  = [verdi*k for verdi in vektor]

def skalarVec(vektor, skalar):
    vec = []
    for i in range(0,len(vektor)):
        vec.append(vektor[i] * skalar)
        #print(vec)
    return vec

def lengdeVec(vektor):
    return math.sqrt(vektor[0]**2 + vektor[1]**2 + vektor[2]**2)

def DotVec(vec1, vec2):
    dot = 0
    for i in range(0,len(vec1)):
        dot += vec1[i] * vec2[i]
    return dot

verdi1 = eval(input("Skriv inn en x-verdi: "))
verdi2 = eval(input("Skriv inn enda en y-verdi: "))
verdi3 = eval(input("Skriv inn en tredje z-verdi: "))
skalerVerdi= int(input("Skriv inn skaleringsverdi: "))

vektor1 = printVec()
skalarProd = skalarVec(vektor1,skalerVerdi)
lengde1 = lengdeVec(vektor1)
lengde2 = lengdeVec(skalarProd)
dotProd = DotVec([1,2,3],[1,2,3])


print("Skalarprodukt = ",skalarProd, sep="")
print("Lengden av vektoren = ", lengde1, sep="")
print("Lengden av den skalerte vektoren = ", lengde2, sep="")
print("Forshold på lengdene = ", lengde2/lengde1, sep="")
print("Dotproduktet er: ", dotProd, sep="")
