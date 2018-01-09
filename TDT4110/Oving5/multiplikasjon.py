__author__ = 'Marius'

def multi():
    tol = 0.01
    i = 1
    prod = 1
    temp = 0
    while prod - temp > tol:
        temp = prod
        prod *= (1+(1/i**2))
        i += 1
    return prod, i

def main():
    svar,iterasjoner = multi()
    print("Sum: ",svar,"\nIterasjoner: ",iterasjoner, sep="")

main()

