#!/usr/bin/python3

from sys import stdin


def max_value(widths, heights, values, paperWidth, paperHeight):
    matrise = [None] * (paperWidth+1)
    for i in range(paperWidth+1):
        matrise[i] = [-1] * (paperHeight+1)

    #Finn den minste høyden eller bredden
    minValue = 999999
    for width in widths:
        minValue = min(minValue, width)
    for height in heights:
        minValue = min (minValue, height)

    #Legger inn null på plass som er "for liten"
    for m in range(minValue):
        for i in range(paperWidth):
            matrise[i][m] = 0
        for i in range(paperHeight):
            matrise[m][i] = 0

    #Legger inn kjente verdier for mulige oppkuttinger
    for v in range(len(values)):
        if widths[v] <= paperWidth and heights[v] <= paperHeight and matrise[widths[v]][heights[v]] < values[v]:
            matrise[widths[v]][heights[v]] = values[v]
        if heights[v] <= paperWidth and widths[v] <= paperHeight and matrise[heights[v]][widths[v]] < values[v]:
            matrise[heights[v]][widths[v]] = values[v]

    for w in range(paperWidth + 1):
        for h in range(paperHeight + 1):
            if matrise[w][h] == 0:
                continue
            if matrise[w][h] == -1:
                best = 0
            else:
                best = matrise[w][h]
            for cutWidth in range(1, w):
                best = max(best,matrise[cutWidth][h] + matrise[w - cutWidth][h])
            for cutHeight in range(1, h):
                best = max(best,matrise[w][cutHeight] + matrise[w][h - cutHeight])
            matrise[w][h] = best

    return matrise[paperWidth][paperHeight]




def main():
    widths = []
    heights = []
    values = []
    for triple in stdin.readline().split():
        dim_value = triple.split(':', 1)
        dim = dim_value[0].split('x', 1)
        width = int(dim[0][1:])
        height = int(dim[1][:-1])
        value = int(dim_value[1])
        widths.append(int(width))
        heights.append(int(height))
        values.append(int(value))
    for line in stdin:
        paper_width, paper_height = [int(x) for x in line.split('x', 1)]
        print((max_value(widths, heights, values, paper_width, paper_height)))

if __name__ == "__main__":
    main()