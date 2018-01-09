poengSum = eval(input("Skriv inn poengsummen din: "))

if (poengSum > 100 or poengSum < 0 or type(poengSum) != int):
    print("Du har skrevet inn en ugyldig poengsum")
elif int(poengSum) >= 89:
    print("Du fikk karakteren A")
elif int(poengSum) >= 77:
    print("Du fikk karakteren B")
elif int(poengSum) >= 65:
    print("Du fikk karakteren C")
elif int(poengSum) >= 53:
    print("Du fikk karakteren D")
elif int(poengSum) >= 41:
    print("Du fikk karakteren E")
elif int(poengSum) >= 0:
    print("Du fikk karakteren F")
    
