package y2024

import java.io.File
import extensions.*
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

class D16 {
    val puzzle = File("../i24/16ss").readLines().map { it.toList() }
    val startState = State(puzzle.size - 2 to 1, Direction.RI)
    val startState2 = State2(puzzle.size - 2 to 1, Direction.RI, setOf())

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
                .filter { puzzle[it.state.loc] != '#' }  // don't run into walls
                // we only add new edges when they arrive at the same state with a lower score
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
            val s2 = solve2(s1)
            println("s2 $s2")
        }
    }

    data class State2 (
        val loc: Dual<Int>,
        val dir: Direction,
        val tiles: Set<Dual<Int>>
    )

    data class Edge2(
        val state: State2,
        val score: Int
    ): Comparable<Edge2> {
        override fun compareTo(other: Edge2) = score.compareTo(other.score)
        override fun equals(other: Any?) = state.equals(other)
        override fun hashCode() = state.hashCode()
    }

    fun Edge2.getNeighbours(): List<Edge2> {
        // moving = 1 point; rotating = 1k points; we can rotate or move
        val dirs = state.dir.rot()
        val move = state.move()
        val newpath = state.tiles + state.loc
        return listOf(
            Edge2(State2(move, state.dir, newpath), score + 1),
            Edge2(State2(state.loc, dirs.first, newpath), score + 1000),
            Edge2(State2(state.loc, dirs.second, newpath), score + 1000),
        )
    }

    fun State2.move(): Dual<Int> {
        return when(dir) {
            Direction.UP -> loc.first - 1 to loc.second
            Direction.DO -> loc.first + 1 to loc.second
            Direction.RI -> loc.first to loc.second + 1
            Direction.LE -> loc.first to loc.second - 1
        }
    }

    fun solve2(best: Int): Int {
        println("solving for $best")
        var edge = Edge2(startState2, 0)
        val queue = PriorityQueue<Edge2>().also{it.add(edge)}
        val visited: MutableMap<State2, Int> = mutableMapOf(edge.state to edge.score)
        val bestTiles = mutableSetOf<Dual<Int>>()

        var x=0

        while (queue.isNotEmpty()) {
            if (queue.size % 1000 == 0) println(queue.size)
            edge = queue.poll()
//            if (edge.score > best) continue
            if (puzzle[edge.state.loc] == 'E') {
                bestTiles.addAll(edge.state.tiles)
                x++
            }
            val neighbours = edge.getNeighbours()
                .filter { puzzle[it.state.loc] != '#' }  // don't run into walls
                // once we are above best we can stop
                .filter { it.score <= best }
                // we only add new edges when they arrive at the same state with a lower score
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
                // we dont need to check paths if all their nodes have already been visited
                .filter {(it.state.tiles - bestTiles).isNotEmpty()}
            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        println("x: $x")
        println("visited: ${visited.size}")

        return bestTiles.size + 1
    }
}

fun main() = D16().main()