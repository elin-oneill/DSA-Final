import json
import matplotlib.pyplot as plt
import networkx as nx
from collections import Counter

def main():
    node_file = open("../data/nodes.json", "r")
    nodes_data = json.loads(node_file.read())
    node_file.close()

    connection_file = open("../data/connections.json", "r")
    connection_data = json.loads(connection_file.read())
    connection_file.close()
    connection_tuples = [tuple(x) for x in connection_data]

    page_rank_file = open("../data/pageRanks.json", "r")
    page_rank_data = json.loads(page_rank_file.read())
    page_rank_file.close()

    G = nx.Graph(connection_tuples)

    # node_sizes = [(page_rank_data[x]) for x in G.nodes]
    page_ranks = [page_rank_data[x] for x in G.nodes]
    min_page_rank = min(page_ranks)
    max_page_rank = max(page_ranks)
    node_sizes = [((300)/(max_page_rank - min_page_rank) * (x - max_page_rank) + 300) for x in page_ranks]

    connection_ends = [i[1] for i in connection_data]
    count = Counter(connection_ends)
    print(count.most_common(10))
    top_count = [i[0] for i in count.most_common(10)]
    labels = dict([[x, x] if x in top_count else [x, ""] for x in G.nodes])

    # print(max(set(connection_ends), key=connection_ends.count))

    nx.draw_networkx(G, node_size=node_sizes, with_labels=False)
    print("drawing graph")
    plt.show()

if __name__ == "__main__":
    main()
