__author__ = 'Marius'
import random
def massemidtpunkt(stang):
    M = 0
    for element in stang:
        M += element

    verdi = 0
    for i in range(0,len(stang)):
        verdi +=stang[i]*(i+0.5)
    return((1/M)*verdi)

def main():
    liste= []
    for i in range(15):
        liste.append(random.randint(1,10))
    verdi = massemidtpunkt(liste)
    print("Massemidtpunktet til en stang med oppdeling ",liste," er ",verdi,sep="")

main()