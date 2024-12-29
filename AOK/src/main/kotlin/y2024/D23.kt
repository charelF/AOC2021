package y2024

import java.io.File
import extensions.*
import kotlin.system.measureTimeMillis

class D23 {
    val connections = File("../i24/23").readLines().map { it.split("-").toSet() }.toSet()
    val vertices = connections.flatten().toSet()

    fun bronKerbosch(R: Set<String>, P: Set<String>, X: Set<String>): Set<Set<String>> {
        if (P.isEmpty() && X.isEmpty()) return setOf(R)
        val Pm = P.toMutableSet()
        val Xm = X.toMutableSet()
        val cliques = mutableSetOf<Set<String>>()
        for (v in P) {
            val neighbours = connections.filter { it.contains(v) }.flatten().toMutableSet() - v
            cliques += bronKerbosch(R + v, Pm intersect neighbours, Xm intersect neighbours)
            Pm -= v
            Xm += v
        }
        return cliques
    }

    fun find3Cliques(): Int {
        return vertices.mapIndexed { i, n1 ->
            vertices.drop(i).mapIndexed { j, n2 ->
                vertices.drop(i+j).map { n3 ->
                    if ((n1.startsWith("t") || n2.startsWith("t") || n3.startsWith("t"))
                        && connections.containsAll(listOf(setOf(n1, n2), setOf(n2, n3), setOf(n3, n1)))
                    ) 1 else 0
                }.sum()
            }.sum()
        }.sum()
    }

    fun main() {
        find3Cliques().print()
        bronKerbosch(setOf(), vertices, setOf()).maxBy { it.size }.sorted().joinToString(",").print()
    }
}

fun main() = D23().main()