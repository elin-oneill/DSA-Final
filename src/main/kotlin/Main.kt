package org.example


/**
 * ``Graph`` represents a directed graph. Taken from my Assignment 3 Code.
 *
 * @param VertexType the type that represents a vertex in the graph
 */
interface Graph<VertexType> {
    /**
     * @return the vertices in the graph
     */
    fun getVertices(): Set<VertexType>

    /**
     * Add an edge between [from] and [to] with edge weight [cost]
     */
    fun addEdge(from: VertexType, to: VertexType, cost: Double)

    /**
     * Get all the edges that begin at [from]
     * @return a map where each key represents a vertex connected to [from] and the value represents the edge weight.
     */
    fun getEdges(from: VertexType): Map<VertexType, Double>

    /**
     * Remove all edges and vertices from the graph
     */
    fun clear()
}

/**
 * ``DirectedWeightedGraph`` represents a directed weighted graph.
 *
 * @param VertexType the type that represents a vertex in the graph
 */
class DirectedWeightedGraph<VertexType>: Graph<VertexType> {
    // Adjacency map: each vertex maps to a map of neighbors and their edge weights
    private val adjacency: MutableMap<VertexType, MutableMap<VertexType, Double>> = mutableMapOf()

    /**
     * Return all vertices in the graph
     */
    override fun getVertices(): Set<VertexType> {
        return adjacency.keys
    }

    /**
     * Add an edge from [from] to [to] with the given [cost].
     * If [from] or [to] are not yet in the graph, they will be added.
     */
    override fun addEdge(from: VertexType, to: VertexType, cost: Double) {
        // Ensure both vertices exist in the adjacency map
        adjacency.putIfAbsent(from, mutableMapOf())
        adjacency.putIfAbsent(to, mutableMapOf()) // so 'to' shows up as a vertex

        // Add or update the edge weight
        adjacency[from]!![to] = cost
    }

    /**
     * Get all outgoing edges from [from].
     * Returns an empty map if the vertex is not found.
     */
    override fun getEdges(from: VertexType): Map<VertexType, Double> {
        return adjacency[from]?.toMap() ?: emptyMap()
    }

    /**
     * Clear the graph of all vertices and edges
     */
    override fun clear() {
        adjacency.clear()
    }
}

/**
 * Compute PageRank values for the given graph.
 *
 * @param graph The directed graph to analyze
 * @param damping The damping factor (typically 0.85)
 * @param maxIterations Maximum number of iterations to run
 * @param tolerance Convergence threshold
 *
 * @return A map from vertex to PageRank value.
 */
fun <VertexType> pageRank(
    graph: Graph<VertexType>,
    damping: Double = 0.85, // the probability that any person will continue following links at a given step
    maxIterations: Int = 100,
    tolerance: Double = 1e-6 // used to check for convergence
): Map<VertexType, Double> {

    val vertices = graph.getVertices()
    val n = vertices.size

    if (n == 0) return emptyMap() // base case

    var ranks = vertices.associateWith { 1.0 / n } // initialize all PRs

    val outgoingEdgesNum = vertices.associateWith { graph.getEdges(it).size } // # of outgoing edges for each pg

    val inbound = vertices.associateWith { mutableListOf<VertexType>() }.toMutableMap() // init list of incoming pgs for each pg

    for (u in vertices) { // populate list
        for (v in graph.getEdges(u).keys) {
            inbound[v]!!.add(u)
        }
    }

    repeat(maxIterations) {
        val newRanks = mutableMapOf<VertexType, Double>()
        var change = 0.0

        for (v in vertices) { // computing the new rank for each pg
            // Sum contributions from all incoming neighbors of v
            val inboundSum = inbound[v]!!.sumOf { u -> // all u's that point to v
                val degree = outgoingEdgesNum[u]!!
                if (degree == 0) 0.0 // u doesn't contribute
                else ranks[u]!! / degree // u contributes to sum: (its rank / its out-degree)
            }

            val newRank = ((1 - damping) / n) + damping * inboundSum
            change += kotlin.math.abs(newRank - ranks[v]!!) // how much v's rank changed
            newRanks[v] = newRank
        }

        ranks = newRanks

        if (change < tolerance) return ranks // check if we've converged
    }

    return ranks
}

/**
 * Example usage of PageRank with DirectedWeightedGraph.
 */
fun main() {
    val graph = DirectedWeightedGraph<String>()
    graph.addEdge("A", "B", 1.0)
    graph.addEdge("B", "C", 1.0)
    graph.addEdge("C", "A", 1.0)
    graph.addEdge("D", "C", 1.0)

    val ranks = pageRank(graph)
    println(ranks)
}
