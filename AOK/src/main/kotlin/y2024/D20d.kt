package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue

class D20d {
    val racetrack = File("../i24/20s").readLines().map { it.toList()}//.slice(1 until it.lastIndex) }.let { it.slice(1 until it.lastIndex) }
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
        val visitedEdges: MutableSet<Edge> = mutableSetOf(edge)
        var b: Int = 0

        while (queue.isNotEmpty()) {
//            println("queue: $queue")
            edge = queue.poll()
//            println(" taking $edge from queue \n")

            if (edge.state.pos == end) {
                println(edge)
                b++
//                println(visitedEdges)
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
                .filter { !visitedEdges.contains(it) }


//            neighbours.forEach { nb ->
//                if (nb.state.pos == 2 to 2) {
//                    println("nb $nb")
//                }
//            }

            val cheatedNeighbours = if (edge.state.cheat is Cheat.AVAILABLE) {
                edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN, 1)
                    .filter { it.isWithin(bounds) }
                    .filter { racetrack[it] == '#' }
                    .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, cheatTime - 2) }
                    .filter { it.isWithin(bounds) }
                    .filter { racetrack[it] == '#' }
                    .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN, 1) }
                    .filter { it.isWithin(bounds) }
                    .filter { racetrack[it] != '#' }
                    .map {
                        Edge(
                            State(it, Cheat.EXPIRED(edge.state.pos, it)),
                            edge.score + it.distanceFrom(edge.state.pos, DistanceMetric.MANHATTAN)
                        )
                    }
                    .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                    .filter { it.score < bestFair[it.state.pos]!! }
                    .filter { it.score <= timeLimit }
                    .filter { !visitedEdges.contains(it) }

//            cheatedNeighbours.forEach { nb ->
//                if (nb.state.pos == 2 to 2) {
//                    println("cheating nb $nb")
//                }
//            }
            } else listOf()

            val x = (neighbours + cheatedNeighbours)//.distinct()
//            println("before distinct:")
//            println(x)
//            println("after distinct:")

            val finalNeighbours = x.distinct()



            for (fn in finalNeighbours) {
                if (fn.state.cheat is Cheat.EXPIRED && fn.state.cheat.end == 7 to 4) {
//                    println("fn $fn")
//                    println("peek ${queue.peek() to queue.toList()}")
                }
            }

//            println("final: $finalNeighbours")

            queue.addAll(finalNeighbours)
            visitedEdges.addAll(finalNeighbours)
            visited.putAll(finalNeighbours.associate { it.state to it.score })

            for (fn in finalNeighbours) {
                if (fn.state.cheat is Cheat.EXPIRED && fn.state.cheat.end == 7 to 4) {
//                    println("fn $fn")
//                    println("peek ${queue.peek() to queue.toList()}")
                }
            }
        }

//        val v2 = visited.mapKeys {it.key.pos}
//        racetrack.mapIndexed { i, line ->
//            line.mapIndexed { j, ch ->
//                v2[i to j] ?: '.'
//            }
//        }.print2D(pad=5)
        println("b: $b")
        println("answer: ${bestEdges.size}")
        println(bestEdges.groupingBy{it.score}.eachCount())
        return false to edge
    }




    fun main() {
//        measureTimeMillis {  x = bfs() }.also { println(it) }
//        measureTimeMillis {  y = dijkstraDistances() }.also { println(it) }
//        println(bfs() == dijkstraDistances())


//        val s1 = Edge(State(1 to 2, Cheat.EXPIRED(3 to 4, 5 to 6)), 8)
//        val s2 = Edge(State(1 to 2, Cheat.EXPIRED(3 to 4, 5 to 6)), 8)
//        println(mapOf(s1 to 1, s2 to 1))
//        return

        val shortest = dijkstra().second.score
        println("shortest (non cheating path): $shortest")
        val distances = bfs()



        dijkstraCheat(distances, 20, shortest-50)
//        dijkstraCheat(distances, 2, shortest-100)
//        dijkstraCheat(distances, 20, 4)//shortest)
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

fun main() = D20d().main()