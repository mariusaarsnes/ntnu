__author__ = 'Marius'
# -*- coding: utf-8 -*-

print("Program for omregning fra Fahrenheit til Celsius.")

def omregning():
    fVerdi = eval(input("Skriv inn temperaturen i Fahrenheit: "))
    cVerdi = round((fVerdi-32) / 1.8, 1)
    print(fVerdi,"grader Fahrenheit er", cVerdi," grader Celsius.", sep=" ")

omregning()

