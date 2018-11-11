__author__ = 'Marius'

def bubbleSort(list):
    for i in range(0,len(list)-1):
        for j in range(i+1,len(list)):
            if list[i] > list[j]:
                temp = list[i]
                list[i] = list[j]
                list[j] = temp
        #print(list)
    return list

def selectionSort(liste):
    nyListe = []
    lengde = len(liste)
    while lengde != len(nyListe):
        storst = -999999999999999
        for i in range(0, len(liste)):
            if liste[i] > storst:
                storst = liste[i]
        nyListe.insert(0,liste.pop(liste.index(storst)))
        #print(nyListe)
    return nyListe

def selectionSortV2(liste):
    nyListe = []
    lengde = len(liste)
    while lengde != len(nyListe):
        nyListe.insert(0,liste.pop(liste.index(max(liste))))
        #print(nyListe)
    return nyListe

def main():
    print(bubbleSort([10,9,8,11,6,5,23,27,2,1,0]), " <-- Bubblesort",  sep="")
    print(selectionSort([10,9,8,11,6,5,23,27,2,1,0])," <-- Selectionsort",  sep="")
    print(selectionSort([10,9,8,11,6,5,23,27,2,1,0])," <-- Selectionsort V2",  sep="")

    print(bubbleSort([3,6,5,1,9,4,5,8,3]), " <-- Bubblesort",  sep="")


main()



