__author__ = 'Marius'
# -*- coding: utf-8 -*-

def wholeNumber(tall):
    if tall % 2 == 0 or tall % 2 == 1:
        verdi = 1
    else:
        verdi = 0
    return verdi

def evenNumber(tall):
    if tall % 2 == 0:
        verdi = 1
    else:
        verdi = 0
    return verdi

def posNegNumber(tall):
    if tall >= 0:
        verdi = 1
    else:
        verdi = 0
    return verdi

def compareNr(verdi1, verdi2):
    if (verdi1 > verdi2) or (verdi1 < verdi2):
        verdi = 0
    else:
        verdi = 1
    return verdi


def utskrift(heltTall, parTall, posNegTall):
    if helTall == 1:
        print("Dette er et heltall")
    else:
        print("Dette er ikke et heltall")
    if parTall == 1:
        print("Dette er et partall")
    else:
        print("Dette er ikke et partall")
    if posNegTall == 1:
        print("Dette er et positivt tall")
    else:
        print("Dette er ikke et positivt tall")

verdi1 = eval(input("Skriv inn et tall: "))

helTall = wholeNumber(verdi1)
parTall = evenNumber(verdi1)
posNegTall = posNegNumber(verdi1)
utskrift(helTall,parTall,posNegTall)

verdi2 = eval (input("Skriv inn enda et tall: "))
helTall = wholeNumber(verdi2)
parTall = evenNumber(verdi2)
posNegTall = posNegNumber(verdi2)
utskrift(helTall,parTall,posNegTall)

likUlik = compareNr(verdi1, verdi2)

if likUlik == 1:
    print("Tallene er like")
else:
    print("Tallene er ikke like")

