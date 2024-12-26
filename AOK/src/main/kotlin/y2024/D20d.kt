package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

class D20d {
    val racetrack = File("../i24/20").readLines().map { it.toList()}//.slice(1 until it.lastIndex) }.let { it.slice(1 until it.lastIndex) }
    val bounds = racetrack.size to racetrack.first().size
    val start = racetrack.dualIndexOf { it == 'S' }
    val end = racetrack.dualIndexOf { it == 'E' }

    val validTrack = racetrack.map { line -> line.map { it != '#' } }

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
        var pos = start
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
//    fun dijkstraDistances(): Map<Dual<Int>, Int> {
//        var edge = Edge(State(end), 0)
//        val queue = PriorityQueue<Edge>().also{it.add(edge)}
//        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)
//
//        while (queue.isNotEmpty()) {
//            edge = queue.poll()
//            if (edge.state.pos == start) continue
//            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
//                .filter { it.isWithin(bounds) }
//                .filter { racetrack[it] != '#'}
//                .map { Edge(State(it), edge.score+1) }
//                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
//
//            queue.addAll(neighbours)
//            visited.putAll(neighbours.associate {it.state to it.score})
//        }
//        return visited.mapKeys { it.key.pos }
//    }

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

    fun dijkstraCheatFast(cheatTime: Int, timeLimit: Int): Map<Int, Int> {
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
                .filter { it.isWithin(bounds) && validTrack[it]}
                .map { Edge(State(it, edge.state.cheat), edge.score+1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                .filter { it.score <= timeLimit}

            val cheatedNeighbours = if (edge.state.cheat is Cheat.AVAILABLE) {
                edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN, 1)
                    // for cheats, we find all edges which are `cheatTime` away, and where we the first neighbour after
                    // we start and the last neighbour before we end is a wall. inbetween those we dont care
                    // and the end must be a non-wall
                    .filter { it.isWithin(bounds) && !validTrack[it]}
                    .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, cheatTime - 2) }
                    .filter { it.isWithin(bounds) && !validTrack[it]}
                    .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, 1) }
                    .filter { it.isWithin(bounds) && validTrack[it]}
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

    fun dijkstraCheatFast2(cheatTime: Int, timeLimit: Int): Map<Int, Int> {
        // Pre-compute valid neighbors for each position to avoid repeated filtering
        val validNeighborsCache = mutableMapOf<Dual<Int>, List<Dual<Int>>>()
        val wallNeighborsCache = mutableMapOf<Dual<Int>, List<Dual<Int>>>()

        // Custom comparator for the priority queue to avoid creating Edge objects
        data class QueueEntry(val pos: Dual<Int>, val score: Int, val cheat: Cheat)
        val queue = PriorityQueue<QueueEntry>(compareBy { it.score })

        // Start state
        queue.offer(QueueEntry(start, 0, Cheat.AVAILABLE))
        val visited = mutableMapOf<Pair<Dual<Int>, Cheat>, Int>()
        visited[start to Cheat.AVAILABLE] = 0

        // Results map
        val cheatedResults = mutableMapOf<Int, Int>()

        // Cache valid neighbors function
        fun getValidNeighbors(pos: Dual<Int>): List<Dual<Int>> {
            return validNeighborsCache.getOrPut(pos) {
                pos.getNeighbours(DistanceMetric.MANHATTAN)
                    .filter { it.isWithin(bounds) && validTrack[it] }
            }
        }

        // Cache wall neighbors function
        fun getWallNeighbors(pos: Dual<Int>): List<Dual<Int>> {
            return wallNeighborsCache.getOrPut(pos) {
                pos.getNeighbours(DistanceMetric.MANHATTAN, 1)
                    .filter { it.isWithin(bounds) && !validTrack[it] }
            }
        }

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val currentState = current.pos to current.cheat

            // Skip if we've found a better path to this state
            if (visited[currentState]!! < current.score) continue

            // Found end state
            if (current.pos == end) {
                cheatedResults[current.score] = cheatedResults.getOrDefault(current.score, 0) + 1
                continue
            }

            // Process regular neighbors
            for (nextPos in getValidNeighbors(current.pos)) {
                val nextScore = current.score + 1
                if (nextScore > timeLimit) continue

                val nextState = nextPos to current.cheat
                if (nextScore < (visited[nextState] ?: Int.MAX_VALUE)) {
                    visited[nextState] = nextScore
                    queue.offer(QueueEntry(nextPos, nextScore, current.cheat))
                }
            }

            // Process cheat neighbors only if cheating is available
            if (current.cheat is Cheat.AVAILABLE) {
                for (wallStart in getWallNeighbors(current.pos)) {
                    val potentialEnds = wallStart.getNeighbours(DistanceMetric.MANHATTAN, cheatTime - 2)
                        .filter { it.isWithin(bounds) && !validTrack[it] }

                    for (wallEnd in potentialEnds) {
                        val validExits = wallEnd.getNeighbours(DistanceMetric.MANHATTAN, 1)
                            .filter { it.isWithin(bounds) && validTrack[it] }

                        for (exit in validExits) {
                            val nextScore = current.score + exit.distanceFrom(current.pos, DistanceMetric.MANHATTAN)
                            if (nextScore > timeLimit) continue

                            val nextState = exit to Cheat.EXPIRED(current.pos, exit)
                            if (nextScore < (visited[nextState] ?: Int.MAX_VALUE)) {
                                visited[nextState] = nextScore
                                queue.offer(QueueEntry(exit, nextScore, Cheat.EXPIRED(current.pos, exit)))
                            }
                        }
                    }
                }
            }
        }

        return cheatedResults
    }




    fun main() {
        val shortest = dijkstra().second.score
        println("shortest (non cheating path): $shortest")
        val distances = bfs() // turns out its not even necessary to do this

        measureTimeMillis{
            dijkstraCheat(cheatTime = 20, timeLimit = shortest-50).print().also { it.values.sum().print() }
        }.print()

        measureTimeMillis{
            dijkstraCheatFast(cheatTime = 20, timeLimit = shortest-50).print().also { it.values.sum().print() }
        }.print()



        // printing
        racetrack.mapIndexed { i, line ->
            line.mapIndexed { j, ch ->
                distances[i to j] ?: '.'
            }
        }.print2D(pad=5)
    }
}

fun main() = D20d().main()