# %%
import pandas as pd
df = pd.read_csv("input", header=None)
(df.diff().fillna(0) > 0).sum()
# %%
