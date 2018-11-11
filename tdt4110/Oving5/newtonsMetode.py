__author__ = 'Marius'

import math
def f(x):
    return ((x-12)*(math.e**(5*x))-8*(x+2)**2)

def g(x):
    return -x-2*x**2-5*x**3+6*x**4

def fDer(x, h):
    return (f(x+(h/2))-f(x+(h/2)))/h

def gDer(x,h):
    return (g(x+(h/2))-g(x+(h/2)))/h


print(f(1))
print(g(1))