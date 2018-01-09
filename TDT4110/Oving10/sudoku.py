__author__ = 'Marius'

brett = [[0 for x in range(9)] for y in range(9)]

def printBrett(brett):
    counter = 0
    print("    0 1 2   3 4 5   6 7 8")
    print("  +-------+-------+-------+")
    for rad in brett:
        if counter == 3 or counter == 6:
            print("  +-------+-------+-------+")
        print(counter,"|",rad[0],rad[1],rad[2],"|",rad[3],rad[4],rad[5],"|",rad[6],rad[7],rad[8],"|",counter)
        counter += 1
    print("  +-------+-------+-------+")
    print("    0 1 2   3 4 5   6 7 8")


def gyldigTrekk(trekk):
    if trekk[0] not in range(10):
        return 0
    if  not isinstance(trekk[1], list):
        return 0
    if trekk[1][0] not in range(9) and trekk[1][1] not in range (9):
        return 0
    return 1


def main():
    print("SPILL SUDOKU:")
    printBrett(brett)
    trekk = input("skriv inn ditt trekk, først tallet du vil sette inn, og se posisjon: (eks. 2, 1 1) \n").split(",")
    print(trekk)
    if gyldigTrekk(trekk):
        print("Gyldig trekk")
    else:
        print("Ugyldig trekk")



main()