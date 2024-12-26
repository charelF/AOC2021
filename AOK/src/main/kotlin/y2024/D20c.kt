package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue

class D20c {
    val racetrack = File("../i24/20s").readLines().map { it.toList() }
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

    fun dijkstraCheat(bestFair: Map<Dual<Int>, Int>, cheatTime: Int, timeLimit: Int): Pair<Boolean, Edge> {
        var edge = Edge(State(start, Cheat.AVAILABLE), 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)
        var bestEdges = mutableSetOf<Edge>()

        while (queue.isNotEmpty()) {

            edge = queue.poll()
            if (edge.state.pos == end) {
                println(queue.size)
                println(edge)
//                best++
                bestEdges.add(edge)
                continue
            } // keep searching
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { Edge(State(it, edge.state.cheat), edge.score+1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                .filter { it.score <= bestFair[it.state.pos]!!}
                .filter { it.score <= timeLimit}

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})

            if (edge.state.cheat is Cheat.EXPIRED) continue // we cant cheat anymore

            val cheatedNeighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN, 1)
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] == '#'}
                .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, cheatTime-2) }
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] == '#'}
                .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, 1) }
                .filter { it.isWithin(bounds) }
                .filter { racetrack[it] != '#'}
                .map { Edge(State(it, Cheat.EXPIRED(edge.state.pos, it)), edge.score + it.distanceFrom(edge.state.pos, DistanceMetric.MANHATTAN)) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                .filter { it.score < bestFair[it.state.pos]!!}
                .filter { it.score <= timeLimit}

            queue.addAll(cheatedNeighbours)
            visited.putAll(cheatedNeighbours.associate { it.state to it.score })
        }

//        val v2 = visited.mapKeys {it.key.pos}
//        racetrack.mapIndexed { i, line ->
//            line.mapIndexed { j, ch ->
//                v2[i to j] ?: '.'
//            }
//        }.print2D(pad=5)
        println("answer: ${bestEdges.size}")
        println(bestEdges.groupingBy{it.score}.eachCount())
        return false to edge
    }




    fun main() {
//        measureTimeMillis {  x = bfs() }.also { println(it) }
//        measureTimeMillis {  y = dijkstraDistances() }.also { println(it) }
//        println(bfs() == dijkstraDistances())



//        println(State(1 to 2, Cheat.EXPIRED(3 to 4, 5 to 6)) == State(1 to 2, Cheat.EXPIRED(3 to 4, 5 to 6)))
//        return

        val shortest = dijkstra().second.score
        println("shortest (non cheating path): $shortest")
        val distances = bfs()



        dijkstraCheat(distances, 20, shortest-50)
        racetrack.mapIndexed { i, line ->
            line.mapIndexed { j, ch ->
                distances[i to j] ?: '.'
            }
        }.print2D(pad=5)

//        val x = -1 to 2
//        val y = 1 to -2
//        println(x.distanceFrom(y, DistanceMetric.MANHATTAN))

    }
}

fun main() = D20c().main()