a = int(input("Skriv inn a-verdi her: "))
b = int(input("Skriv inn b-verdi her: "))
c = int(input("Skriv inn c-verdi her: "))

d = b**2 - (4*a*c)
if ( d < 0):
    melding = " har to imaginære løsninger."
elif (d > 0):
    melding = " har to reelle løsninger."
else:
    melding = " har èn reell dobbeltrot"

print ( a,"x^2+",b,"x+",c,"= 0",melding)

    
    
