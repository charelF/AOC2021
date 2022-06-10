# %%
from __future__ import annotations  # used for postponed evaluation: https://stackoverflow.com/a/42845998/9439097

class Node:
    """
        A node has children and a parent
        all other values are in a dictionary
    """

    def __init__(self, parent: Node) -> None:
        self.parent = parent
        self.children = []
        self.values = dict()

def main():

    # testing node
    node = Node(None)
    print(node.values)

    tree = Node(None)
    tree.children

main()



# %%
