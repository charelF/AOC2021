# %%

I = open("inputs22/16t").read().split("\n")

paths = {}

for line in I:
    _, valve, _, _, rate, _, _, _, _, *nextvalves = line.split(" ")
    nextvalves = [n.split(",")[0] for n in nextvalves]
    rate = int(rate[5:-1])
    print(valve, rate, nextvalves)
    paths[valve] = (rate, False, nextvalves)

paths


import heapq as hq

H = []
hq.heappush(H, (0, "AA"))

data = {v:0 for v in paths.keys()}

timeleft =

# what do we care when we move to a specific valve:
# time + gas elapsed
# limited time -> make most gas escape
#

while H:
    value, valve = hq.heappop(H)
    rate, isopen, nextpaths = paths[valve]

    if isopen:
        totalrate = 0
    else:
        # TODO: takes 1 minute to open it
        totalrate += rate

    








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

# idea:
# start in 
# does it make sense to keep track of the path?





# %%
