__author__ = 'Marius'

cheeses = {
'cheddar':
('A235-4', 'A236-1', 'A236-2', 'A236-3', 'A236-5', 'C31-1', 'C31-2'),
'mozarella':
('Q456-9', 'Q456-8', 'A234-5', 'Q457-1', 'Q457-2'),
'gombost':
('ZLAFS55-4', 'ZLAFS55-9', 'GOMBOS-7', 'A236-4'),
'geitost':
('SPAZ-1', 'SPAZ-3', 'EMACS45-0'),
'port salut':
('B15-1', 'B15-2', 'B15-3', 'B15-4', 'B16-1', 'B16-2', 'B16-4'),
'camembert':
('A243-1', 'A234-2', 'A234-3', 'A234-4', 'A235-1', 'A235-2', 'A235-3'),
'ridder':
('GOMBOS-4', 'B16-3'),
}

def finnAlleHyller(osteNavn ="port salut"):
    return cheeses[osteNavn]

def finnAlleInfiserte(infiserteHyller=['A234','A235', 'B13','B14','B15','C31']):
    infiserteOster = []
    for ost in cheeses:
        for infisertHylle in infiserteHyller:
            if any(infisertHylle in hylle for hylle in finnAlleHyller(ost)):
                infiserteOster.append(ost)
                break
    return infiserteOster

def finnAlleUinfiserte():
    uinfiserteOster = []
    infiserteOster = finnAlleInfiserte()
    for ost in cheeses:
        if not(ost in infiserteOster):
            uinfiserteOster.append(ost)
    return uinfiserteOster

def printAlleUinfiserte():
    uinfiserteOster = finnAlleUinfiserte()
    for ost in cheeses:
        if ost in uinfiserteOster:
            for i in range(0,len(cheeses[ost])):
                print(cheeses[ost][i],": ",ost,sep="")
print(finnAlleHyller())
print(finnAlleInfiserte())
print(printAlleUinfiserte())
