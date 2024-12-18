package y2024

import java.io.File
import extensions.*
import y2024.D16.Edge
import y2024.D16.State
import java.util.PriorityQueue

class D18 {
    val fallen = 1024//12
    val bytes = File("../i24/18").readLines()
        .flatMap{ it.split(",") }
        .chunked(2).map { it[0].toInt() to it[1].toInt() }.take(fallen).print()
    val start = 0 to 0
    val end = 70 to 70 //6 to 6

    data class Edge(
        val state: Dual<Int>,
        val score: Int
    ): Comparable<D18.Edge> {
        override fun compareTo(other: D18.Edge): Int = score.compareTo(other.score)
    }

    fun solve1(): Edge {
        var edge = Edge(start, 0)
        val queue = PriorityQueue<Edge>().also{ it.add(edge) }
        val visited: MutableMap<Dual<Int>, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state == end) break
            val neighbours = edge
                .state.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(end + (1 to 1))}
                .filter { !bytes.contains(it) }
                .map { Edge(it, edge.score + 1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return edge
    }
}

fun main() {
    D18().solve1().print()
}