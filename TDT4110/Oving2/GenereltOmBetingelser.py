a = 5
b = float(input("Skriv inn et tall her: "))

if a*b < a+b:
    print(str(a*b) + " er mindre enn " + str(a+b))
elif a*b > a+b:
    print(str(a+b) + " er mindre enn " + str(a*b)) 
else:
    print("Summenog produktet av " + str(a) + " og " + str(b) +" er lik.")

svar = input("Hva er 3*4? ")

if float(svar) == 3*4:
    print("riktig")
else:
    print("galt")

