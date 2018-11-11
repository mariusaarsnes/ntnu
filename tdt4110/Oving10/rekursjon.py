__author__ = 'Marius'


def sumRekke(n):
    if n > 1:
        return n * sumRekke(n-1)
    else:
        return 1

print(sumRekke(2))

def minsteElement(liste = [5,2,9,4], minste = None):
    if minste == None:
        minste = liste[0]

    if liste:
        if minste > liste[0]:
            minste = liste[0]
        liste.remove(liste[0])
        return minsteElement(liste,minste = minste)
    else:
        return minste

print(minsteElement())

