__author__ = 'Marius'
# -*- coding: utf-8 -*-


def addere(tall1, tall2):
    sum = tall1 + tall2
    return sum

def subtrahere(tall1, tall2):
    diff = tall1 - tall2

    return diff

def multiplisere(tall1, tall2):
    produkt = tall1 * tall2

    return produkt

def dividere(tall1,tall2):
    kvotient = tall1 / tall2

    return kvotient

operasjon = str(input("Velg hvilken operasjon du vil utføre. "
                      "(Du kan velge mellom 'addere', 'subtrahere', multiplisere' og 'dividere'): "))
tall1 = eval(input("Skriv inn et tall: "))
tall2 = eval(input("Skriv inn enda et tall: "))

if operasjon.lower() == "addere":
    svar = addere(tall1,tall2)

elif operasjon.lower() == "subtrahere":
    svar = subtrahere(tall1,tall2)
elif operasjon.lower() == "multiplisere":
    svar = multiplisere(tall1,tall2)
elif operasjon.lower() == "dividere":
    svar = dividere(tall1,tall2)
else:
    print("Du har skrevet inn en ugyldig operasjonsmetode")
    quit()

print(svar)