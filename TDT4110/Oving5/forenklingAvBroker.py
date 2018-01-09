__author__ = 'Marius'

def gcd(a,b):
    while b != 0:
        tempB = b
        b = a%b
        a = tempB
    return a

def reduceFraction(a,b):
    sfd = gcd(a,b)
    return int(a/sfd), int(b/sfd)

verdi1 = int(input("Skriv inn et positivt heltall: "))
verdi2 = int(input("Skriv inn enda et positivt heltall: "))

a,b = reduceFraction(verdi1,verdi2)
print(a,"/",b,sep="")

