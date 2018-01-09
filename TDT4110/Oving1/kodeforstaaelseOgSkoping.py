
x = 5
y = 8
def main ():
    x = 7
    y = 3
    print ("i main :", x, y)
    swap (x,y)
    print ("i main :", x, y)
    printglobals ()
    print ("i main :", x, y)
def swap (x, y):
    x,y = y,x # Python triks for å bytte om to variabler .
    print (" ---> i swap :", x, y)
def printglobals ():
    print (" ---> i printglobals :", x, y)
main ()
