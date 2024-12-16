package y2024

import java.io.File
import extensions.*
import java.util.PriorityQueue

class D16 {
    enum class Direction {
        UP { override fun rot() = LE to RI },
        DO { override fun rot() = LE to RI },
        RI { override fun rot() = UP to DO },
        LE { override fun rot() = UP to DO };
        abstract fun rot(): Dual<Direction>
    }

    data class Edge (
        val score: Int,
        val loc: Dual<Int>,
        val dir: Direction
    ): Comparable<Edge> {
        override fun compareTo(other: Edge): Int {
            return this.score.compareTo(other.score)
        }
    }

    fun Edge.move(): Dual<Int> {
        return when(dir) {
            Direction.UP -> loc.first - 1 to loc.second
            Direction.DO -> loc.first + 1 to loc.second
            Direction.RI -> loc.first to loc.second + 1
            Direction.LE -> loc.first to loc.second - 1
        }
    }

    fun Edge.getNeighbours(): List<Edge> {
        // moving = 1 point; rotating = 1k points; we can rotate or move
        val dirs = dir.rot()
        val move = move()
        return listOf(
            Edge(score + 1, move, dir),
            Edge(score + 1000, loc, dirs.first),
            Edge(score + 1000, loc, dirs.second)
        )
    }

    fun main() {
        val puzzle = File("../i24/16s").readLines().map { it.toList() }.print()
        val start = puzzle.size - 2 to 1
        val end = 1 to puzzle.first().size -2
        println(puzzle[start] to puzzle[end])

        val queue = PriorityQueue<Edge>()
        var edge = Edge(0, start, Direction.RI).print()
        val visited: MutableMap<Edge, Int> = mutableMapOf(edge to 0)
        queue.add(edge)
        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (puzzle[edge.loc] == 'E') break
            val nb = edge.getNeighbours()
                .filter { puzzle[it.loc] != '#' }
                .filter { e -> e.score < (visited[e] ?: Int.MAX_VALUE) }
            queue.addAll(nb)
            visited.putAll(nb.associateWith { it.score })
        }
        println(edge)
    }
}

fun main() = D16().main()