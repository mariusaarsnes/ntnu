from sys import stdin

Inf = 1000000000


def min_coins_greedy(coins, value):
    coinCount = 0
    i = 0
    lenCoins = len(coins)
    while i < lenCoins:
        if (value >= coins[i]):
            coinCount += 1
            value -= coins[i]
        if (value < coins[i]):
            i +=1
    return coinCount




def min_coins_dynamic(coins, value):
    # SKRIV DIN KODE HER
    coins.reverse()
    matrix = []
    for i in range(value+1):
        matrix.append(Inf)
    matrix[0] = 0
    for s in range(1, value+1):
        for j in range(lenCoins):
            if (coins[j] <= s and matrix[s-coins[j]]+1 < matrix[s]):
                matrix[s] = matrix[s-coins[j]]+1
    return matrix[value]




def can_use_greedy(coins):
    # bare returner False her hvis du ikke klarer aa finne ut
    # hva som er kriteriet for at den graadige algoritmen skal fungere
    # SKRIV DIN KODE HER
    for i in range(lenCoins-1):
        if coins[i] % coins[i+1] != 0:
            return False
    return True

coins = []
for c in stdin.readline().split():
    coins.append(int(c))
coins.sort()
coins.reverse()
lenCoins = len(coins)
method = stdin.readline().strip()
if method == "graadig" or (method == "velg" and can_use_greedy(coins)):
    for line in stdin:
        print(min_coins_greedy(coins, int(line)))
else:
    for line in stdin:
        print(min_coins_dynamic(coins, int(line)))
