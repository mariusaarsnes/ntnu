h = 10**-3
x = 3.14
import math
f1 = math.sin(x)
f2 = math.sin(x+h)
deriv1 = (f2-f1)/h
print(deriv1)

def regnUt(h1,x1):
    import math
    f1 = math.sin(x1)
    f2 = math.sin(x1+h1)
    deriv1 = (f2-f1)/h1
    print(deriv1)

h1 = float(input("skriv inn h-verdi her: "))
x1 = float(input("Skriv inn x-verdi her: "))

regnUt(h1,x1)

