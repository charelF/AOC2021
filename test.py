import numpy as np

s = open("inputs22/8").read()

t = [[int(j) for j in i] for i in s.split("\n")]
print(t)
F = np.array(t)
print(F)

v = 0

N, M = F.shape

for i in range(N):
    for j in range(M):
        val = F[i,j]
        if ((val > F[:i,j]).all()
        or  (val > F[i+1:,j]).all()
        or  (val > F[i,:j]).all()
        or  (val > F[i,j+1:]).all()):
            v += 1

print(v)

def compute_ssc(slice):
    if slice.shape[0] <= 1: return 1
    tree = slice[0]
    ss = 1
    ret = None
    for next_tree in slice[1:]:
        print(next_tree)
        if next_tree >= tree:
            ret = ss
        else:
            ss += 1
    if not ret: ret = ss-1
    print(slice, ret)
    return ret


    # print(slice)
    # t = slice[0]
    # ss = 1
    # pi = slice[1]
    # if pi > t: return 1
    # for i in slice[2:]:
    #     if i <= t:
    #         pi = i
    #         ss += 1
    #     else:
    #         break
    # return ss

SF = np.zeros_like(F)

for i in range(N):
    for j in range(M):
# i = 1
# j = 2
        # if i != 2 or j != 0: continue
        val = F[i,j]



        up = compute_ssc(F[:i+1,j][::-1])
        left = compute_ssc(F[i,:j+1][::-1])
        down = compute_ssc(F[i:,j])
        right = compute_ssc(F[i,j:])

        print(val, (i,j), "==>", up, down, left, right)


        SF[i,j] = up * down * left * right

print(SF)
print(np.max(SF[1:-1,1:-1]))


# import pandas as pd

# dic = {("k1", "k2"): 0.1, ("k21", "k22"): 0.2}

# data = [[k1, k2, k3] for (k1, k2), k3 in dic.items()]

# print(data)

# print(pd.DataFrame(data))



