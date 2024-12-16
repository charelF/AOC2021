package y2024

import java.io.File
import extensions.*
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

class D16 {
    val puzzle = File("../i24/16").readLines().map { it.toList() }
    val startState = State(puzzle.size - 2 to 1, Direction.RI)

    enum class Direction {
        UP { override fun rot() = LE to RI },
        DO { override fun rot() = LE to RI },
        RI { override fun rot() = UP to DO },
        LE { override fun rot() = UP to DO };
        abstract fun rot(): Dual<Direction>
    }

    data class State (
        val loc: Dual<Int>,
        val dir: Direction
    )

    data class Edge(
        val state: State,
        val score: Int
    ): Comparable<Edge> {
        override fun compareTo(other: Edge) = score.compareTo(other.score)
        override fun equals(other: Any?) = state.equals(other)
        override fun hashCode() = state.hashCode()
    }

    fun State.move(): Dual<Int> {
        return when(dir) {
            Direction.UP -> loc.first - 1 to loc.second
            Direction.DO -> loc.first + 1 to loc.second
            Direction.RI -> loc.first to loc.second + 1
            Direction.LE -> loc.first to loc.second - 1
        }
    }

    fun Edge.getNeighbours(): List<Edge> {
        // moving = 1 point; rotating = 1k points; we can rotate or move
        val dirs = state.dir.rot()
        val move = state.move()
        return listOf(
            Edge(State(move, state.dir), score + 1),
            Edge(State(state.loc, dirs.first), score + 1000),
            Edge(State(state.loc, dirs.second), score + 1000),
        )
    }

    fun solve1(): Edge {
        var edge = Edge(startState, 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (puzzle[edge.state.loc] == 'E') break
            val neighbours = edge.getNeighbours()
                .filter { puzzle[it.state.loc] != '#' }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return edge
    }

    fun main() {
        measureTimeMillis {
            val s1 = solve1().score
            println(s1)
        }.print()

    }

//    fun solve2(best: Int) {
//        var edge = Edge(0, start, Direction.RI)
//        var path = listOf(edge)
//        val queue = PriorityQueue<Edge>().also{it.add(edge)}
//        val visited: MutableMap<Edge, Int> = mutableMapOf(edge to 0)
//
//
//    }
}

fun main() = D16().main()