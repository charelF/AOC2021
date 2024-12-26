package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue
import java.util.Stack
import kotlin.collections.addAll
import kotlin.system.measureTimeMillis

class D20b {
    val racetrack = File("../i24/20").readLines().map { it.toList() }
    val bounds = racetrack.size to racetrack.first().size
    val start = racetrack.dualIndexOf { it == 'S' }
    val end = racetrack.dualIndexOf { it == 'E' }

    data class State (
        val pos: Dual<Int>,
    )

    data class Edge(
        val state: State,
        val score: Int
    ): Comparable<Edge> {
        override fun compareTo(other: Edge) = score.compareTo(other.score)
    }

    /**
     * dijkstra finds the shortest path only, we return once we find it
     */
    fun dijkstra(): Pair<Boolean, Edge> {
        var edge = Edge(State(start), 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == end) return true to edge
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { Edge(State(it), edge.score+1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return false to edge
    }


    /**
     * with breadth-first-search we can find the distances from each path to the end
     * by starting at the end and exploring all neighbours recursively
     */
    fun bfs(): Map<Dual<Int>, Int> {
        var state = State(end)
        val queue = mutableListOf<State>().also{ it.add(state) }
        val discovered: MutableMap<State, Int> = mutableMapOf(state to 0)
        while (queue.isNotEmpty()) {
            state = queue.removeFirst() // remove first is essential for bfs
            // we need to use it like a queue
            // if we removeLast(), its dfs instead
            val neighbours = state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { State(it) }
                .filter { !discovered.contains(it) }

            queue.addAll(neighbours)
            discovered.putAll(neighbours.associate {it to discovered[state]!! + 1})
        }
        return discovered.mapKeys { it.key.pos }
    }

    /**
     * we can also use dijkstra to find all distances if we dont stop once we find the best
     * path but instead continue until the queue is empty
     */
    fun dijkstraDistances(): Map<Dual<Int>, Int> {
        var edge = Edge(State(end), 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == start) continue
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { Edge(State(it), edge.score+1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return visited.mapKeys { it.key.pos }
    }




    fun main() {
//        measureTimeMillis {  x = bfs() }.also { println(it) }
//        measureTimeMillis {  y = dijkstraDistances() }.also { println(it) }
        println(bfs() == dijkstraDistances())
        val distances = bfs()

        racetrack.mapIndexed { i, line ->
            line.mapIndexed { j, ch ->
                distances[i to j] ?: '.'
            }
        }.print2D(pad=5)
    }
}

fun main() = D20b().main()