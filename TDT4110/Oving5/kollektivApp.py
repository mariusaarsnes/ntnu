__author__ = 'Marius'

print("App for kollektivtransport\n\n")

alder = int(input("Skriv inn din alder: "))

if alder <5 or alder >60:
    pris= 0
elif alder <21:
    pris = 20
elif alder <26:
    pris = 50
elif alder <61:
    pris = 80

print ("Du må betale", pris, "kr for bussbillett.", sep=" ")
