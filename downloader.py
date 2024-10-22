# %%

session_cookie = "53616c7465645f5fe39750ee1a722563c081ea41543a6faa25bd9ba73dbd7c1534e6d174553f93a67a7574aead1bee8a3c27425a3de28db847e3db8464855a99"

import requests

headers = {"Cookie": "session=" + session_cookie}
url = "https://adventofcode.com/2023/day/1/input"
r = requests.get(url, headers=headers)
