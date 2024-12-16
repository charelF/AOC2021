package y2024

import java.io.File
import extensions.*
import java.util.PriorityQueue

class D16 {
    val puzzle = File("../i24/16").readLines().map { it.toList() }
    val start = puzzle.size - 2 to 1
    val end = 1 to puzzle.first().size -2

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
        // the two functions below are there so in the visited edges container we know which ones are equal
        // (same hash) which depends only on loc and dir, not score. this way we dont re-search worse edges
        override fun equals(other: Any?): Boolean {
            return (other is Edge) && other.loc == loc && other.dir == dir
        }
        override fun hashCode(): Int {
            return (100000 * loc.hashCode()) + dir.hashCode()
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

    fun solve1(): Edge {
        var edge = Edge(0, start, Direction.RI)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<Edge, Int> = mutableMapOf(edge to 0)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (puzzle[edge.loc] == 'E') break
            val neighbours = edge.getNeighbours()
                .filter { puzzle[it.loc] != '#' }
                .filter { e -> e.score < (visited[e] ?: Int.MAX_VALUE) }
            queue.addAll(neighbours)
            visited.putAll(neighbours.associateWith { it.score })
        }
        return edge
    }

    fun main() {
        val s1 = solve1().score
        println(s1)
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