# %%
f = open("inputs22/14").read().split("\n")
f = [i.split(" -> ") for i in f]
f = [[i.split(",") for i in j] for j in f]
f = [[(int(i), int(j)) for i,j in k] for k in f]

import numpy as np
import matplotlib.pyplot as plt

C = np.zeros((200, 1000)).astype(int)

maxy = 2

for line in f:
    for i in range(len(line)-1):
        t1 = line[i]
        t2 = line[i+1]

        ystart = min(t1[1], t2[1])
        yend = max(t1[1], t2[1]) + 1
        xstart = min(t1[0], t2[0])
        xend = max(t1[0], t2[0]) + 1

        maxy = max(maxy, yend)

        # print(t1[1],t2[1]+1, t1[0],t2[0]+1)

        C[ystart:yend, xstart:xend] = 1

maxy += 1

C1 = C.copy()
C2 = C.copy()

C2[maxy] = 1



def sandtrickle(C):
    sx, sy = 500, 0
    while True:
        left, middle, right = C[sy+1, sx-1:sx+2]
        # print(sx, sy, left, middle, right )
        match left, middle, right:
            case (_, 0, _):
                sy = sy + 1
            case (0, 1, _):
                sx = sx - 1
                sy = sy + 1
            case (1, 1, 0):
                sx = sx + 1
                sy = sy + 1
            case (1, 1, 1):
                C[sy, sx] = 1
                return C


i = 0
try:
    while True:
        C1 = sandtrickle(C1)
        i += 1
except IndexError:
    print(i)

for i in range(100*100_000):
    C2 = sandtrickle(C2)
    i += 1
    if C2[0,500] == 1:
        break
print(i)




def printC(C):
    for line in C:
        for item in line:
            if item == 1:
                print("o", end="")
            if item == 0:
                print(".", end="")
        print()
        

printC(C[:15, 80:120])