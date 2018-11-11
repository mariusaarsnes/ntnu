msgNr = 0
def logg(tid, navn,melding):
    global msgNr
    msgNr += 1
    loggTekst = "Msg " + str(msgNr) + "," +  tid + " sendte " + navn + " foelgende melding: " + melding + "."
    print (loggTekst)

def main():    
    logg("23:59","Mr. Y", "Har du mottat pakken?")
    logg("0:00","Mdm. Evil","Pakken er mottatt.")
    logg("0:03","Dr. Horrible","All you need is love!")
    logg("0:09","Me. Y","Dr. Horrible, Dr. Horrible, calling Dr. Horrible .")
    logg("0:09","Mr. Y","Dr. Horrible, Dr. Horrible wake up now.")
    logg("0:09","Dr. Horrible","Up now!")

main()


print("hei","på","deg", sep="ho")    
