__author__ = 'Marius'

sporreListe = {"antKvinner": 0, "antMenn": 0, "antFag": 0, "antITGK": 0, "antTimerLekser": 0 }

while True:
    fag = ""
    print("SPØRREUNDERSØKELSE:")

    kjonn = str(input("Skriv inn ditt kjønn (f eller m): "))

    #Undersøker hva brukeren har svart
    #Hvis svaret ikke er f eller m sjekker om svaret er "hade"
    #øker riktig kjønn-teller med 1
    if kjonn.lower() != "f" and kjonn.lower() != "m":
        if kjonn.lower() == "hade":
            break
        else:
            print("Du har skrevet inn en ugyldig verdi")
            continue

    #Ber om alder og undersøker om alder er i riktig aldersgruppe
    alder= input("Denne undersøkelsen er beregnet for personer i alderen mellom 16 og 25 år, skriv inn din alder: ")
    if alder == "hade":
        break
    if int(alder) < 16 or int(alder) > 25:
        print("Du er ikke innen aldersgruppen ")
        continue

    #Undersøker om bruker tar fag
    #og om h*n tar ITGK
    while fag != "nei" and fag != "ja":
        fag = str(input("Har du noen fag? (ja/nei): "))

        if fag =="hade":
            break
        elif fag == "ja":
            if int(alder) < 22:
                itgk = input("Tar du ITGK? (ja/nei) ")
            elif int(alder) > 22:
                itgk = input("Tar virkelig du ITGK? (ja/nei) ")
            if itgk =="hade":
                break;
            timerLekse= input("Hvor mange timer jobber du med lekse, i snitt, om dagen ? ")
            if timerLekse =="hade":
                break;
    if kjonn == "f":
        sporreListe["antKvinner"] += 1
    elif kjonn == "m":
        sporreListe["antMenn"] += 1
    if fag == "ja":
        sporreListe["antFag"] += 1
        if itgk == "ja":
            sporreListe["antITGK"] += 1
        sporreListe["antTimerLekser"] += int(timerLekse)

print(sporreListe["antKvinner"] , sporreListe["antMenn"] , sporreListe["antFag"]
      , sporreListe["antITGK"] , sporreListe["antTimerLekser"], sep= "  ")