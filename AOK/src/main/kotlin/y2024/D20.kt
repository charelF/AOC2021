package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue

class D20 {
    val racetrack = File("../i24/20").readLines().map { it.toList()}//.slice(1 until it.lastIndex) }.let { it.slice(1 until it.lastIndex) }
    val bounds = racetrack.size to racetrack.first().size
    val start = racetrack.dualIndexOf { it == 'S' }
    val end = racetrack.dualIndexOf { it == 'E' }

    sealed class Cheat() {
        data object AVAILABLE: Cheat()
        data class EXPIRED(val start: Dual<Int>, val end: Dual<Int>): Cheat()
    }

    data class State (
        val pos: Dual<Int>,
        val cheat: Cheat
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
        var edge = Edge(State(start, Cheat.AVAILABLE), 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == end) return true to edge
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { Edge(State(it, Cheat.AVAILABLE), edge.score+1) }
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
        var pos = end
        val queue = mutableListOf<Dual<Int>>().also{ it.add(pos) }
        val discovered: MutableMap<Dual<Int>, Int> = mutableMapOf(pos to 0)
        while (queue.isNotEmpty()) {
            pos = queue.removeFirst() // remove first is essential for bfs
            // we need to use it like a queue
            // if we removeLast(), its dfs instead
            val neighbours = pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .filter { !discovered.contains(it) }

            queue.addAll(neighbours)
            discovered.putAll(neighbours.associate {it to discovered[pos]!! + 1})
        }
        return discovered
    }

    /**
     * we can also use dijkstra to find all distances if we dont stop once we find the best
     * path but instead continue until the queue is empty
     */
    fun dijkstraDistances(): Map<Dual<Int>, Int> {
        var edge = Edge(State(end, Cheat.AVAILABLE), 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == start) continue
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { Edge(State(it, Cheat.AVAILABLE), edge.score+1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return visited.mapKeys { it.key.pos }
    }

    /**
     * dijkstraCheat uses dijkstra and the fact that neighbours can chose to cheat
     * to find the best cheated path. However the state space is very big and my implemenation is
     * inefficient so it only works for the small sample input.
     */
    fun dijkstraCheat(cheatTime: Int, timeLimit: Int): Map<Int, Int> {
        var edge = Edge(State(start, Cheat.AVAILABLE), 0)
        val queue = PriorityQueue<Edge>().also{ it.add(edge) }
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)
        var cheatedResults = mutableMapOf<Int, Int>()

        while (queue.isNotEmpty()) {
            edge = queue.poll()

            if (edge.state.pos == end) {
                cheatedResults[edge.score] = cheatedResults.getOrDefault(edge.score, 0) + 1
                continue
            } // but keep searching

            val regularNeighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) && racetrack[it] != '#'}
                .map { Edge(State(it, edge.state.cheat), edge.score+1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                .filter { it.score <= timeLimit}

            val cheatedNeighbours = if (edge.state.cheat is Cheat.AVAILABLE) {
                edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN, 1)
                    // for cheats, we find all edges which are `cheatTime` away, and where we the first neighbour after
                    // we start and the last neighbour before we end is a wall. inbetween those we dont care
                    // and the end must be a non-wall
                    .filter { it.isWithin(bounds) && racetrack[it] == '#' }
                    .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, cheatTime - 2) }
                    .filter { it.isWithin(bounds) && racetrack[it] == '#' }
                    .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, 1) }
                    .filter { it.isWithin(bounds) && racetrack[it] != '#' }
                    .map {
                        Edge(
                            State(it, Cheat.EXPIRED(edge.state.pos, it)),
                            edge.score + it.distanceFrom(edge.state.pos, DistanceMetric.MANHATTAN)
                        )
                    }
                    .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                    .filter { it.score <= timeLimit }
            } else listOf()

            val neighbours = (regularNeighbours + cheatedNeighbours).distinct()

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate { it.state to it.score })

        }
        return cheatedResults
    }

    /**
     * Got this idea from reddit - apparently cheats are not really related to walls, except that they must start and
     * end on a non-wall. The algorithm below just computes the distances between each point ('.') and every other
     * point within [cheatTime] distance, then checks that the jump between those points (including the distance needed
     * to walk to them, which is at most [cheatTime]) saves more than [timeToBeSaved] steps. If yes, we count them
     * as unique cheat shortcut.
     */
    fun compareCoordinates(distances: Map<Dual<Int>, Int>, cheatTime: Int, timeToBeSaved: Int): Int {
        val cheats = mutableMapOf<DualDual<Int>, Int>()
        racetrack.forEachIndexed { i, line ->
            line.forEachIndexed { j, ch ->
                val point = i to j
                val pointDistance = distances[point]
                if (pointDistance != null) {
                    val neighbours = point.getNeighbours(DistanceMetric.MANHATTAN, cheatTime)
                    neighbours.forEach { nb ->
                        val neighbourDistance = distances[nb]
                        if (neighbourDistance != null) {
                            val saved = (pointDistance - neighbourDistance) - point.distanceFrom(nb, DistanceMetric.MANHATTAN)
                            if (saved >= timeToBeSaved) {
                                cheats[(i to j) to nb] = saved
                            }
                        }
                    }
                }
            }
        }
        return cheats.values.groupingBy {it}.eachCount().values.sum()
    }

    fun main() {
        val shortest = dijkstra().second.score
        val distances = bfs()

        // below is my idea - its correct but too slow
        // dijkstraCheat(cheatTime = 20, timeLimit = shortest-50).print().also { it.values.sum().print() }

        println("part 1: ${compareCoordinates(distances, 2, 100)}")
        println("part 2: ${compareCoordinates(distances, 20, 100)}")
    }
}

fun main() = D20().main()