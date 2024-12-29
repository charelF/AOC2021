package y2024

import java.io.File
import extensions.*
import kotlin.system.measureTimeMillis

class D24 {
    val parts = File("../i24/24").readText().split("\n\n")
    val known = parts.first().lines().associate {
        it.split(": ")
            .let { (wire, value) -> wire to (value == "1") }
    }.toMap()
    val gates = parts.last().lines().associate {
        it.split(" ").let { (w1, op, w2, _, w3) ->
            when (op) {
                "XOR" -> w3 to Gate(w1, w2, op, Boolean::xor, w3)
                "AND" -> w3 to Gate(w1, w2, op, Boolean::and, w3)
                "OR" -> w3 to Gate(w1, w2, op, Boolean::or, w3)
                else -> TODO()
            }
        }
    }

    data class Gate (
        val lhs: String,
        val rhs: String,
        val op: String,
        val exec: (Boolean, Boolean) -> Boolean,
        val res: String,
    )

    fun f1() {
        val known = known.toMutableMap()
        var prev = 0
        var diff: Int = known.size - prev
        while (diff != 0) {
            for (gate in gates.values) {
                if (gate.lhs in known && gate.rhs in known) {
                    known[gate.res] = gate.exec(known[gate.lhs]!!, known[gate.rhs]!!)
                }
            }
            diff = known.size - prev
            prev = known.size
        }
        known.filterKeys { it.startsWith('z') }
            .toSortedMap().reversed().values.map { it.toInt() }
            .joinToString("")
            .toLong(2)
            .also { println("part 1: $it") }
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

    fun generator() {
        val sortedGates = toposort(gates.values.toList())

        fun makeIndexer(s: String): String {
            if (s.startsWith('x') || s.startsWith('y') || s.startsWith('z')) {
                return "${s.first()}[${s.drop(1).toInt()}]"
            }
            return s
        }

        for (g in sortedGates) {
            val glhs = makeIndexer(g.lhs)
            val grhs = makeIndexer(g.rhs)
            val gres = makeIndexer(g.res)
            val start = if ('[' in gres) gres else "val $gres"
            val strOp = when(g.op) {
                "AND" -> "&&"
                "OR" -> "||"
                "XOR" -> "xor"
                else -> TODO()
            }
            println("$start = $glhs $strOp $grhs")
        }
    }

    fun main() {
        f1()
        f2()

        val y = listOf(1,1,1,1,0,1,0,1,0,1,1,1,1,0,0,0,0,1,1,1,0,1,1,1,1,1,1,0,1,1,0,0,0,1,1,0,0,0,0,1,0,0,0,0,1).map { it == 1 }
        val x = listOf(1,0,0,1,1,1,0,0,0,1,0,0,0,1,0,1,1,0,0,1,1,1,0,1,1,1,1,1,1,0,1,1,0,0,0,1,1,0,0,0,0,0,0,1,1).map { it == 1 }

        fx(gates.values.toList(), x, y)
    }


    /**
     * not needed in the end, could be used to run the binary adder
     */
    fun fx(gates: List<Gate>, x: List<Boolean>, y: List<Boolean>): List<Int> {
        val sortedGates = toposort(gates)
        val known: MutableMap<String, Boolean> = mutableMapOf()

        for (gate in sortedGates) {
            val glhs = if (gate.lhs.startsWith('x')) x[gate.lhs.drop(1).toInt()] else if (gate.lhs.startsWith('y')) y[gate.lhs.drop(1).toInt()] else known[gate.lhs]!!
            val grhs = if (gate.rhs.startsWith('x')) x[gate.rhs.drop(1).toInt()] else if (gate.rhs.startsWith('y')) y[gate.rhs.drop(1).toInt()] else known[gate.rhs]!!
            known[gate.res] = gate.exec(glhs, grhs)
        }
        return known.filterKeys { it.startsWith('z') }
            .toSortedMap().values.toList().map { it.toInt() }
    }

    fun f2() {
        // following https://www.reddit.com/r/adventofcode/comments/1hla5ql/2024_day_24_part_2_a_guide_on_the_idea_behind_the/
        // 1) If the output of a gate is z, then the operation has to be XOR unless it is the last bit.
        val wrong1 = gates.values.filter { gate ->
            gate.res.contains('z') && gate.op != "XOR" && gate.res != "z45"
        }

        // 2) If the output of a gate is not z and the inputs are not x, y then it has to be AND / OR, but not XOR.
        val wrong2 = gates.values.filter { gate ->
            !gate.res.contains('z') && !gate.lhs.contains('x') && !gate.rhs.contains('x') && !gate.lhs.contains('y') && !gate.rhs.contains('y') && gate.op == "XOR"
        }

        // now also this comment: https://www.reddit.com/r/adventofcode/comments/1hla5ql/comment/m3kws15/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
        // If you have a XOR gate with inputs x, y, there must be another XOR gate with this gate as an input.
        // Search through all gates for an XOR-gate with this gate as an input; if it does not exist, your (original) XOR gate is faulty.
        val wrong3 = gates.values.filter { gate ->
            if ((gate.lhs.contains('x') || gate.rhs.contains('x') || gate.lhs.contains('y') || gate.rhs.contains('y')) && gate.op == "XOR") {
                // then there there must be another XOR gate with this gate as an input
                // find gates which have this gate as input
                gates.values.none { (it.lhs == gate.res || it.rhs == gate.res) && it.op == "XOR" }
            } else false
        }.filter { !it.lhs.contains("00") }

        // Similarly, if you have an AND-gate, there must be an OR-gate with this gate as an input.
        // If that gate doesn't exist, the original AND gate is faulty.
        val wrong4 = gates.values.filter { gate ->
//            if (gate.op == "AND") {
            if ((gate.lhs.contains('x') || gate.rhs.contains('x') || gate.lhs.contains('y') || gate.rhs.contains('y')) && gate.op == "AND") {
                gates.values.none { (it.lhs == gate.res || it.rhs == gate.res) && it.op == "OR" }
            } else false
        }.filter { !it.lhs.contains("00") }
        // we see that one of wrong4 was already found by wrong1 or wrong2
        // so must be the other


        // we need to swap the gates of wrong1 with the gates of wrong2
        // im too lazy to understand how so ill just try all combinations
        // we want to try each swap of the 3 z with the 3 others
        // first z - 3 options, 2nd z - 2 options for each 1st z, last z - no options
        // below is a crazy convoluted way of getting those 6 options lol
        val swaps = (wrong1 + wrong2).map { it.res }
            .combinations(ChooseBy.UNIQUE)
            .map { lst -> lst.chunked(2) }
            .filter { listOfChunks -> listOfChunks.all { chunks -> chunks.first().startsWith('z') } }
            .map { it.toSet() }
            .toSet()

        // actually since we just have 8 values, this is the answer, we dont need to generate swaps and run those lol

        // dont even need the swaps we just print all the wrongs and look at the unique ones
        (wrong1 + wrong2 + wrong3 + wrong4).map { it.res }.distinct()
            .sorted()
            .joinToString(",")
            .also { println("part 2: $it")}
    }
}

fun main() {
    D24().main()
}

