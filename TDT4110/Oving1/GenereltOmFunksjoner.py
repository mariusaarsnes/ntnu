def multiply(x,y):
    x*=y
    return x

def skrivTilSkjerm(innParameter):
    print(innParameter)

def return2():
    return 2

x = int(input("Skriv inn et tall her: "))
y = int(input("Skriv enda et tall her: "))
innParameter = input("Skriv noe her: ")
oppgaveA = multiply(x,y)
print(oppgaveA)

skrivTilSkjerm(innParameter)

oppgaveC = return2()
print(oppgaveC)
