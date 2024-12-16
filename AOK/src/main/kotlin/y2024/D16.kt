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

    // this structure is just there for the queue
    data class Edge(
        val state: State,
        val score: Int
    ): Comparable<Edge> {
        override fun compareTo(other: Edge) = score.compareTo(other.score)
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
                .filter { puzzle[it.state.loc] != '#' }  // don't run into walls
                // we only add new edges when they arrive at the same state with a lower score
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return edge
    }

    fun solve2(best: Int): Int {
        var e2 = E2(Edge(startState, 0), setOf())
        val queue = PriorityQueue<E2>().also{ it.add(e2) }
        val visited: MutableMap<State, Int> = mutableMapOf(e2.edge.state to e2.edge.score)
        val bestTiles = mutableSetOf<Dual<Int>>()

        while (queue.isNotEmpty()) {
            e2 = queue.poll()
            if (puzzle[e2.edge.state.loc] == 'E') bestTiles.addAll(e2.hist)
            val neighbours = e2.getNeighbours()
                .filter { puzzle[it.edge.state.loc] != '#' }  // don't run into walls
                // dont have to consider neighbours above the best score
                .filter { it.edge.score <= best }
                // we only add new edges when they arrive at the same state with an equal or lower score
                .filter { it.edge.score <= (visited[it.edge.state] ?: Int.MAX_VALUE) }

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.edge.state to it.edge.score})
        }
        return bestTiles.size + 1
    }

    fun main() {
        measureTimeMillis {
            val s1 = solve1().score
            println("part 1: $s1")
            val s2 = solve2(s1)
            println("part 2: $s2")
        }.print()
    }

    data class E2(
        val edge: Edge,
        val hist: Set<Dual<Int>>
    ): Comparable<E2> {
        override fun compareTo(other: E2) = edge.compareTo(other.edge)
    }

    fun E2.getNeighbours(): List<E2> = edge.getNeighbours().map { edge -> E2(edge, hist + edge.state.loc) }
}

fun main() = D16().main()