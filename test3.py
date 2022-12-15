# %%

inp = [[ord(i) for i in j] for j in open("inputs22/13").read().split("\n")]
# inp2 = 

import numpy as np
I = np.array(inp)
I = np.pad(I, pad_width=1, mode='constant', constant_values=1000)

# start = np.argwhere(I==83)
# end = np.argwhere(I==69)
# print(end)

# I[end] == ord("z")  # end is z below
# print(start, end)
# end = (end[0]+1, end[1]+1)
# print(I)

for i in range(I.shape[0]):
    for j in range(I.shape[1]):
        if I[i,j] == 69:
            end = i,j
            I[end] = ord("z")
        if I[i,j] == 83:
            start = i,j
print(end)

def get_neighbours_of(i,j):
    current = I[i,j]
    n = []

    for ni, nj in zip([i-1, i+1, i, i], [j,j,j-1,j+1]):
        match I[ni,nj]:
            case 83: continue
            case val:
                if val <= current+1:
                    n.append((ni,nj))
                if current == 83:
                    if val != 1000:
                        n.append((ni,nj))

    return n

import heapq as hq

inf = 100_000



D = np.full_like(I,  inf)
H = []
hq.heappush(H, (0, start))
while H:
    value, coords = hq.heappop(H)
    if D[coords] == inf:  # not yet visited
        # if D[coords] < value:
        #     print(1)
        D[coords] = value
        if coords == end:
            print("DONE")
            break
        for neighbour in get_neighbours_of(*coords):
            if D[neighbour] > (D[coords] + 1):  # replace current path if shorter path exists
                hq.heappush(H, (D[coords] + 1, neighbour))

D2 = np.where(D==inf, -1, D)
print(np.max(D2))

# part 2

D = np.full_like(I,  inf)
H = []
all_starts = np.argwhere(I == ord("a"))
print(all_starts)

for start in all_starts:
    hq.heappush(H, (0, tuple(start)))

while H:
    value, coords = hq.heappop(H)
    if D[coords] == inf:  # not yet visited
        # if D[coords] < value:
        #     print(1)
        D[coords] = value
        if coords == end:
            print("DONE")
            break
        for neighbour in get_neighbours_of(*coords):
            if D[neighbour] > (D[coords] + 1):  # replace current path if shorter path exists
                hq.heappush(H, (D[coords] + 1, neighbour))

D2 = np.where(D==inf, -1, D)
print(np.max(D2))




# import matplotlib.pyplot as plt
# plt.imshow(D2)

# guesses 456 457 all too high

# %%

