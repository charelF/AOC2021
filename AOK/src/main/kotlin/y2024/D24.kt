package y2024

import java.io.File
import extensions.*
import kotlin.system.measureTimeMillis

class D24 {
    val parts = File("../i24/24").readText().split("\n\n")
    val known = parts.first().lines().associate {
        it.split(": ")
            .let { (wire, value) -> wire to (value == "1") }
    }.toMutableMap()
    val gates = parts.last().lines().map {
        it.split(" ").let { (w1, op, w2, _, w3) ->
            when (op) {
                "XOR" -> Gate(w1, w2, Boolean::xor, w3)
                "AND" -> Gate(w1, w2, Boolean::and, w3)
                "OR" -> Gate(w1, w2, Boolean::or, w3)
                else -> TODO()
            }
        }
    }

    data class Gate (
        val lhs: String,
        val rhs: String,
        val op: (Boolean, Boolean) -> Boolean,
        val res: String,
    )

    fun f1() {
        var prev = 0
        var diff: Int = known.size - prev
        while (diff != 0) {
            for (gate in gates) {
                if (gate.lhs in known && gate.rhs in known) {
                    known[gate.res] = gate.op(known[gate.lhs]!!, known[gate.rhs]!!)
                }
            }
            diff = known.size - prev
            prev = known.size
        }
        known.filterKeys { it.startsWith('z') }
            .toSortedMap().reversed().values.map { it.toInt() }
            .joinToString("")
            .toLong(2)
            .print()
        println(45213383376616)
    }

    enum class TSMark { NONE, TEMP, PERM }
    data class TSNode (
        val gate: Gate,
        var mark: TSMark
    ) {
        fun getDescendants(network: List<TSNode>): List<TSNode> {
            // find all nodes X where there is an edge from this to X
           return network.filter { it.gate.lhs == gate.res || it.gate.rhs == gate.res }
        }
    }

    /**
     * using the DFS-based algo from https://en.wikipedia.org/wiki/Topological_sorting
     */
    fun toposort(gates: List<Gate>): List<Gate> {
        val nodes = gates.map { TSNode(it, TSMark.NONE) }
        val l: MutableList<TSNode> = mutableListOf()
        val unmarked = {node: TSNode -> node.mark == TSMark.NONE  }

        fun visit(node: TSNode) {
            if (node.mark == TSMark.PERM) return
            if (node.mark == TSMark.TEMP) throw Exception("graph has cycle; cant do toposort")
            node.mark = TSMark.TEMP
            // for each node m with an edge from n to m, visit m
            node.getDescendants(nodes).forEach { visit(it) }
            node.mark = TSMark.PERM
            l.addFirst(node)
        }

        // while exists nodes without a permanent mark do
        while (nodes.any(unmarked)) {
            val node = nodes.first(unmarked)
            visit(node)
        }
        return l.map {it.gate}
    }

    fun main() {
        f1()

        toposort(gates).printVert()
    }
}

fun main() {
    D24().main()
}

