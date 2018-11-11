__author__ = 'Marius'
import random
numbers = [i for i in range(1,35)]
myGuess = []
lottoNumbers = []
#print(numbers,"\n", myGuess, sep="")

def tippLottoTall():
    i = 0
    while i < 10:    #Går fra 0 til 9, fordi vi vil ha 10 tall. syv lottotall og 3 tilleggstall
        nytt = False
        while nytt != True:
            print("Tall du allerede har lagt til i tippekupongen: ", myGuess, sep="")
            x = int(input("Skriv en tall du vil tippe: "))
            if i == 0: #    Hvis det er det første tallet brukes det uansett.
                #print("Første tallet!")
                nytt = True
                break
            else:
                #print("ikke første")
                for j in range(0,len(myGuess)):
                    #print(len(newList))
                    if x != myGuess[j]:
                        nytt = True
                    else:
                        nytt = False
                        break
        myGuess.append(x)
        i += 1

def trekkLottoTall():
    i = 0
    while i < 10:    #Går fra 0 til 9, fordi vi vil ha 10 tall. syv lottotall og 3 tilleggstall
        nytt = False
        while nytt != True:
            x = random.randint(1,34)
            if i == 0: #    Hvis det er det første tallet brukes det uansett.
                #print("Første tallet!")
                nytt = True
                break
            else:
                #print("ikke første")
                for j in range(0,len(lottoNumbers)):
                    #print(len(newList))
                    if x != lottoNumbers[j]:
                        nytt = True
                    else:
                        nytt = False
                        break
        lottoNumbers.append(x)
        i += 1

def compList(myGuess, lottoNumbers):
    likeLottoTall = 0
    likeEkstraTall = 0
    for i in range(0,len(myGuess)-4):
        for j in range(0, len(lottoNumbers)-4):
            if myGuess[i] == lottoNumbers[j]:
                likeLottoTall +=1
                break
    for i in range(len(myGuess)-4, len(myGuess)):
        for j in range(len(lottoNumbers)-4, len(lottoNumbers)):
            if myGuess[i] == lottoNumbers[j]:
                likeEkstraTall += 1
                break
    return likeLottoTall, likeEkstraTall

def premieFunc(likeTall1, likeTall2):
    if likeTall1 == 7:
        premie = 2749455
    elif likeTall1 == 6 and likeTall2 == 1:
        premie = 102110
    elif likeTall1 == 6:
        premie = 3385
    elif likeTall1 == 5:
        premie = 95
    elif likeTall1 == 4 and likeTall2 == 1:
        premie = 45
    else:
        premie = 0
    return premie

def main():
    print("LOTTOTIPPING")
    tippLottoTall()
    print(myGuess)
    trekkLottoTall()
    print(lottoNumbers)
    likeTall1, likeTall2 = compList(myGuess, lottoNumbers)
    premie = premieFunc(likeTall1,likeTall2)
    print("Du hadde ", likeTall1, " tall, og ", likeTall2, " tillegggstall rett.\nDu har vunnet: ",premie, "kr.", sep="")
    #print(likeTall1," - ", likeTall2, sep="")

main()



