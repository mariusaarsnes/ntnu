__author__ = 'Marius'

dagListe = ["man","tir","ons","tor","fre","lor","son"]

def is_leap_year ( year ):
    if year % 400 == 0:
        return True
    elif year % 100 == 0:
        return False
    elif year % 4 == 0:
        return True
    return False

def weekdayNewyear(year):
    d = 0
    for i in range(1900,year+1):
        if i == year:
            return d
        #print(i, dagListe[d], sep=" - ")
        if is_leap_year(i):
            if d == 6:
                d = 1
            elif d ==5:
                d =0
            else:
                d += 2
        else:
            if d == 6:
                d = 0
            else:
                d +=1

def isWorkday(weekday):
    for i in range(0,6):
        if weekday == dagListe[i] and i < 5:
            return True
        elif weekday == dagListe[i] and i >= 5:
            return False

def workdaysInYear(year):
    forsteDag = weekdayNewyear(year)
    if is_leap_year(year):
        if forsteDag < 3:
            utskrift = 5*52+2
        elif forsteDag < 5:
            utskrift = 5*52+1
        elif  forsteDag <6:
            utskrift = 5*52
        else:
            utskrift = 5*52+1
    else:
        if forsteDag <5:
            utskrift = 5*52+1
        else:
            utskrift = 5*52
    return utskrift

dag = str(input("Hvilken dag ønsker du å finne ut om er arbeidsdag eller ikke? "))
if isWorkday(dag):
    print("ja\n")
else:
    print("nei\n")

aar = int(input("Hvilket år ønsker du å sjekke? "))

forsteDag =weekdayNewyear(aar)

print(aar,dagListe[forsteDag], sep=" - ")
print("")


for i in range(1900,1920):
    forsteDag = weekdayNewyear(i)
    print(i, dagListe[forsteDag], sep=" - ")

print("")


antArbeidsDager = workdaysInYear(aar)
print(aar, "har", antArbeidsDager, "arbeidsdager.", sep=" ")
print("")

'''
for i in range (1900,1920):
    antArbeidsDager = workdaysInYear(i)
    print(i, "har", antArbeidsDager, "arbeidsdager.", sep=" ")
'''

