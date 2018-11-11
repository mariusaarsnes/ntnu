def overflateAreal(a):
    A = int(3**(1/2) * a**2)
    # Orker ikke alle desimalene, så derfor setter jeg A til å være integer.
    return A
def volum(a):
    V = int((2**(1/2)*a**3)/12)
    # Orker ikke alle desimalene, så derfor setter jeg V til å være integer.
    return V

oppgaveA = overflateAreal(4)
print(oppgaveA)

oppgaveB = volum(4)
print(oppgaveB)

h = int(input("skriv inn hoeyden på tetraederet her: "))

a = 3/(6**(1/2)) * h

oppgaveC = "Volum: " + str(volum(a)) + "  Areal: " + str(overflateAreal(a))

print(oppgaveC)

print(format(12.3214312, '.2f'))
