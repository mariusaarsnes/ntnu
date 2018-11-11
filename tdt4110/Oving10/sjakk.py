__author__ = 'Marius'

def printBrett():
    for rad in brettV2:
        print(rad)

def erGyldigTrekk(trekk, spiller, brikke):
    if brikke[0][0] != spiller[0].lower():
        return False
    else:
        return True



sjakkMatt = False
spiller = "Hvit"
print("Spill sjakk!")
print("For å bevege en brikke:\nSkriv inn posisjonen til brikken og hvor du vil flytte den.")
print("Eksempel: B2,B4")
print("Hvit begynner!")

bokstavTilTall = {"a":1,"b":2,"c":3,"d":4,"e":5,"f":6,"g":7,"h":8}

brikkeBevegelseRetning = {"B": [[0,1],[1,1]],
                          "T": [[0,1],[1,0]],
                          "H": [1,1],
                          "L": [1,1],
                          "D": [[0,1],[1,0],[1,1]],
                          "K": [[0,1],[1,0],[1,1]]
                          }


brettV2 = [["  "," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8","  "],
           [" a","hT","hB","  ","  ","  ","  ","sB","sT"," a"],
           [" b","hH","hB","  ","  ","  ","  ","sB","sH"," b"],
           [" c","hL","hB","  ","  ","  ","  ","sB","sL"," c"],
           [" d","hD","hB","  ","  ","  ","  ","sB","sD"," d"],
           [" e","hK","hB","  ","  ","  ","  ","sB","sK"," e"],
           [" f","hL","hB","  ","  ","  ","  ","sB","sL"," f"],
           [" g","hH","hB","  ","  ","  ","  ","sB","sH"," g"],
           [" h","hT","hB","  ","  ","  ","  ","sB","sT"," h"],
           ["  "," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8","  "]
           ]

printBrett()
while not sjakkMatt:
    trekk = input(spiller +", velg brikke og hvor du vil flytte den:\n")
    trekk = trekk.split(",")
    brikke = brettV2[bokstavTilTall[trekk[0][0]]][int(trekk[0][1])]

    if erGyldigTrekk(trekk,spiller,brikke):
        brettV2[bokstavTilTall[trekk[1][0]]][int(trekk[1][1])] = brettV2[bokstavTilTall[trekk[0][0]]][int(trekk[0][1])]
        brettV2[bokstavTilTall[trekk[0][0]]][int(trekk[0][1])] = "  "
        printBrett()
    else:
        print("Ugyldig trekk, prøv igjen")
        continue

    #   Skifter mellom spillere (svart og hvit)

    if spiller == "Hvit":
        spiller = "Svart"
    else:
        spiller ="Hvit"