package y2024

import java.io.File
import extensions.*
import y2024.D16.Edge
import y2024.D16.State
import java.util.PriorityQueue

class D18 {
    val bytes = File("../i24/18").readLines()
        .flatMap{ it.split(",") }
        .chunked(2).map { it[0].toInt() to it[1].toInt() }
    val start = 0 to 0
    val end = 70 to 70

    data class Edge(
        val state: Dual<Int>,
        val score: Int
    ): Comparable<D18.Edge> {
        override fun compareTo(other: D18.Edge): Int = score.compareTo(other.score)
    }

    fun solve(fallen: Int): Edge {
        val fallenBytes = bytes.take(fallen)
        var edge = Edge(start, 0)
        val queue = PriorityQueue<Edge>().also{ it.add(edge) }
        val visited: MutableMap<Dual<Int>, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state == end) break
            val neighbours = edge
                .state.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(end + (1 to 1))}
                .filter { !fallenBytes.contains(it) }
                .map { Edge(it, edge.score + 1) }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }
            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return edge
    }

    fun main() {
        solve(1024).also { println("part 1: $it") }

        // binary search
        var l = 1024
        var r = bytes.lastIndex
        var i = -1
        while (true) {
            i = (l + r) / 2
            if (l == r) break
            if (solve(i).state == end) l = i + 1
            else r = i
        }
        // not sure why i - 1
        println("part 2: ${bytes[i - 1]} at pos ${i - 1}")
    }
}

fun main() {
    D18().main()
}