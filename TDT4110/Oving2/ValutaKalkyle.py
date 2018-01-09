__author__ = 'Marius'
# -*- coding: utf-8 -*-


valutaKursListe = {'NÅ' : {'EURO' : 8.7 , 'GBP' : 11.9 , 'RUB' : 0.14},
                   'SENERE' : {'EURO' : 9.1 , 'GPB' : 12.5, 'RUB' : 0.15}}
gebyrListe = {'FLYPLASS' : 10, 'BANK' : 5}

def konverter(tall,valuta,tid,plass):
    global valutaKursListe
    global gebyrListe
    resultat  = tall / valutaKursListe[tid.upper()][valuta.upper()] * ((100-gebyrListe[plass.upper()])/100)
    return (resultat)

verdi = float(input("Skriv inn verdien du vil ha konvertert: "))
valuta = str(input("Skriv inn valutaen du vil konvertere til (Du kan velge mellom; EURO, GPB, RUB): "))
tid = str(input("Velg når du vil veksle inn pengene (Du kan velge mellom 'nå' og 'senere'): "))
plass = str(input("Velg et sted du vil veksle (Du kan velge mellom 'flyplass' og 'bank'): "))

resultat = konverter(verdi,valuta,tid,plass)

print("Veksling av ", verdi, " NOK til ",valuta," gir: ",resultat, sep="")