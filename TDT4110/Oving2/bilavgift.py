__author__ = 'Marius'
# -*- coding: utf-8 -*-

print("Program for utregning av nettopris på bil.")

def main():
    navn = str(input("Skriv inn navnet på bilen: "))
    bruttoPris = eval(input("Skriv inn bruttoprisen på bilen: "))
    vekt = eval(input("Skriv inn vekten på bilen, i kg: "))
    antHK = eval(input("Skriv inn antall hestekrefter: "))
    utslipp = eval(input("Skriv inn utslipp av CO2, i gram: "))
    motorVolum = eval(input("Skriv inn motorvolum på bilen: "))
    beregnAvgift(navn, bruttoPris,vekt,antHK,utslipp,motorVolum)

def beregnAvgift(navn, bruttoPris, vekt, antHK, utslipp, motorVolum):
    Vekt_p = bruttoPris*vekt*0.00034
    Hk_p = bruttoPris*antHK*0.00015
    CO2_p = bruttoPris*utslipp*0.004
    Volum_p = bruttoPris*motorVolum*0.00055
    nettoPris = bruttoPris + Vekt_p + Hk_p + CO2_p + Volum_p
    print("Nettoprisen for", navn, "vil bli: ", nettoPris, sep=" ")

main()