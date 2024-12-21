package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue


class D20 {
    val racetrack = File("../i24/20s").readLines().map { it.toList() }
    val bounds = racetrack.size to racetrack.first().size
    val start = racetrack.dualIndexOf { it == 'S' }
    val end = racetrack.dualIndexOf { it == 'E' }
    val startState = State(start, 1)
    val walls = racetrack.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, ch ->
            if (ch=='#') i to j else null
        }
    }

    data class State (
        val pos: Dual<Int>,
        val cheatsRemaining: Int,
    )

    data class Edge(
        val state: State,
        val score: Int
    ): Comparable<Edge> {
        override fun compareTo(other: Edge) = score.compareTo(other.score)
    }

    fun solve(track: List<List<Char>>, limit: Int): Pair<Boolean, Edge> {
        var edge = Edge(startState, 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == end) return true to edge
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .map { Edge(State(it, edge.state.cheatsRemaining), edge.score+1) }
                .filter { track[edge.state.pos] != '#'}
                .filter { it.score <= limit } // needs to be cheat otherwise lame
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }

//            val cheatedNeighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
//                .filter { it.isWithin(bounds) }
//                .filter { edge.state.cheatsRemaining > 0 } // uneffcient but whatever
//                .map { Edge(State(it, edge.state.cheatsRemaining - 1), edge.score+1) }
//                .filter { track[edge.state.pos] == '#'}
//                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
//            val neighbours = realNeighbours + cheatedNeighbours

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return false to edge
    }

    fun main() {
        val shortest = solve(racetrack, Int.MAX_VALUE)
        println(shortest)
        println("total walls: ${walls.size}")

        val improvement = 40

        walls.mapIndexed { i, wall ->
            println(i)
            val track = racetrack.map { it.toMutableList() }.toMutableList()
            track[wall] = '.'
            solve(track, shortest.second.score - improvement).first.toInt()
        }.sum().print()
    }
}

fun main() {
    D20().main()

    // q1 = 1411
}