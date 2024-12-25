package y2024

import java.io.File
import extensions.*

class D24 {
//    data class Node<T> (
//        val node: T,
//        val children: Dual<Node<T>>
//    )

    init {
        val (p1, p2) = File("../i24/24s").readText().split("\n\n")
        val known = p1.lines().associate { it.split(": ")
            .let { (wire, value) -> wire to (value == "1") }
        }.toMutableMap()
        val gates = p2.lines().associate { it.split(" ")
            .let { (w1, op, w2, _, w3) -> setOf(w1, w2) to (op to w3) }
        }
        val unknown = (gates.flatMap { (k,v) -> listOf(k.first(), k.last(), v.second) }.toMutableSet() - known.keys).toMutableSet()

        while (unknown.isNotEmpty()) {
            println(unknown.size)
            gates.filter { (k,v) -> known.keys.containsAll(k) && unknown.contains(v.second) }.forEach { (k,v) ->
                when(v.first) {
                    "AND" -> known[v.second] = known[k.first()]!! && known[k.last()]!!
                    "XOR" -> known[v.second] = known[k.first()]!! xor known[k.last()]!!
                    "OR" -> known[v.second] = known[k.first()]!! || known[k.last()]!!
                }
                unknown -= known.keys
            }
            unknown -= known.keys
            println(unknown)
        }
        println(known)






    }
//    val split = inp5.split("\n\n")
//    val register =
//        split.first().split("\n").associate {
//    val wires = split.last().split("\n").associate {
//        it.split(" ").let { (w1, op, w2, _, w3) ->
//            when(op) {
//                "AND" -> Triple(w1,w2,w3) to BinOp.AND
//                "XOR" -> Triple(w1,w2,w3) to BinOp.XOR
//                "OR" -> Triple(w1,w2,w3) to BinOp.OR
//                else -> TODO()
//            }
//        }
//    }.toMutableMap()
}

fun main() {
    D24()
}