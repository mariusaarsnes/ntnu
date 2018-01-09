__author__ = 'Marius'


def sparingB(A, r,k, n=4):
    return (A *(1+((r/100)/n))**(n*k))

def sparingC(A, r, k, n=4):
    spareListe = []
    for i in range (0,k+1):
        spareListe.append(sparingB(A,r,i))
    return spareListe

def sparingD(sluttSum):
    tempSlutt = 0
    startSum = 0
    print(sluttSum)
    while tempSlutt < sluttSum:
        startSum += 1000
        tempSlutt =sparingB(startSum,5,20)
    return startSum


investeringSum = input("Skriv inn summen du vil investere: \n")
rente = input("Hva er den nominelle renten? \n")
antallAar = input("Hvor mange år?\n")

liste = sparingC(int(investeringSum), int(rente), int(antallAar))


for i in range(1, len(liste)):
    print(i,":",liste[i])

sluttSum = eval(input("skriv inn ønsket sluttsum: \n"))

print("For å ha spart opp til ", sluttSum, ", må du investere ", sparingD(sluttSum),"kr som startsum.", sep="")


