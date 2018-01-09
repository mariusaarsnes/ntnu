__author__ = 'Marius'
import math

primListe = [2]

def delelig(a,b):
    if a%b == 0:
        return True
    else:
        return False

def isPrime(a):
    b = 3
    if delelig(a,2):
        print("delelig på 2")
        return 0
    while b < round(math.sqrt(a)+0.5):
        if delelig(a,b):
            return 0
        else:
            b+=2
    return 1

def isPrimeListe(a):
    global primListe
    for b in primListe:
        if a == b:
            return
        if delelig(a,b):
            return
    primListe.append(a)
    return

def main():
    print(isPrime(5))
    for i in range(3,12):
        isPrimeListe(i)
    print(primListe)

main()