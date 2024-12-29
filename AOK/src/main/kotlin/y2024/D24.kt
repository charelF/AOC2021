package y2024

import java.io.File
import extensions.*

class D24 {

    data class Gate (
        val lhs: String,
        val rhs: String,
        val op: (Boolean, Boolean) -> Boolean,
        val res: String,
    )

    init {
        val (p1, p2) = File("../i24/24").readText().split("\n\n")
        val known = p1.lines().associate {
            it.split(": ")
                .let { (wire, value) -> wire to (value == "1") }
        }.toMutableMap().print()

        val gates = p2.lines().map {
            it.split(" ").let { (w1, op, w2, _, w3) ->
                when (op) {
                    "XOR" -> Gate(w1, w2, Boolean::xor, w3)
                    "AND" -> Gate(w1, w2, Boolean::and, w3)
                    "OR" -> Gate(w1, w2, Boolean::or, w3)
                    else -> TODO()
                }
            }
        }.print()

        repeat(100) {
            println(known.size)
            for (gate in gates) {
                if (gate.lhs in known && gate.rhs in known) {
                    known[gate.res] = gate.op(known[gate.lhs]!!, known[gate.rhs]!!)
                }
            }
        }

        known.filterKeys { it.startsWith('z') }
            .toSortedMap().reversed().values.map { it.toInt() }
            .joinToString("")
            .toLong(2)
            .print()
    }
}

fun main() {
    D24()
}