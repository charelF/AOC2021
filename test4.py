# %%

I = open("inputs22/15").read().split("\n")

coords = []

const = 2_000_000
size = 5_000_000

for line in I:
    _, l1, l2, l3, by = line.split("=")
    bx, _ = l3.split(", ")
    sx, _ = l1.split(", ")
    sy, _ = l2.split(":")
    sx = int(sx) + const
    sy = int(sy) + const
    bx = int(bx) + const
    by = int(by) + const

    coords.append(((sx, sy), (bx, by)))

print(coords)

import numpy as np
import matplotlib.pyplot as plt
G = np.zeros((size,size), dtype=int)

for ((sx, sy), (bx, by)) in coords:
    G[sy,sx] = 10
    G[by,bx] = 20

plt.imshow(G)

def get_d_away(x,i,j):
    coords2 = []
    for c1 in range(0, x+1):
        c2 = x - c1
        coords2.append((i+c1, j+c2))
        coords2.append((i-c1, j+c2))
        coords2.append((i+c1, j-c2))
        coords2.append((i-c1, j-c2))
    return set(coords2)

print(get_d_away(2, 0, 0))


    
print(G)


def draw_signals(G, i, j):
    d = 0
    breakout = False
    while not breakout:
        new_areas = get_d_away(d, i, j)
        for coord in new_areas:
            if G[coord] == 10:
                continue
            if G[coord] == 20:
                breakout = True
            if G[coord] in [0,1]:
                G[coord] = 1
        d += 1
    return G

# G = draw_signals(G)

for ((sx, sy), (bx, by)) in coords:
    G = draw_signals(G, sy, sx)


plt.imshow(G)

ysol = 10 + const
np.sum(G[ysol] != 0) - np.sum(G[ysol] == 20)






# %%


