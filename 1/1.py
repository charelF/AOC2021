# %%
import pandas as pd
df = pd.read_csv("input", header=None)
# task 1: window = 1
# task 2: window = 3
(df.rolling(3).sum().dropna().diff().fillna(0) > 0).sum()
# %%
