package y2024

import extensions.*

class D21 {
    enum class Key { UP, DO, RI, LE, KA }
    enum class Num { B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, BA }


    val numPad: List<List<Num?>> = listOf(
        listOf(Num.B7, Num.B8, Num.B9),
        listOf(Num.B4, Num.B5, Num.B6),
        listOf(Num.B1, Num.B2, Num.B3),
        listOf(null,   Num.B0, Num.BA),
    )
    val numPadValidFields = numPad.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, v ->
            if (v != null) i to j else null
        }
    }

    val keyPad: List<List<Key?>> = listOf(
        listOf(null, Key.UP, Key.KA),
        listOf(Key.RI, Key.DO, Key.LE),
    )
    val keyPadValidFields = keyPad.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, v ->
            if (v != null) i to j else null
        }
    }

    fun getShortestPath(from: Num, to: Num): List<Key> {
        val startIdx = numPad.dualIndexOf {it == from}.print()
        val pathMap: MutableMap<Dual<Int>, List<Key>> = mutableMapOf(startIdx to listOf())
        repeat(5) {
            val snapshot = pathMap.toMap()
            for ((idx, path) in snapshot) {
                val (i, j) = idx
                val neighbours = mapOf<Dual<Int>, List<Key>>(
                    (i + 1 to j) to path + Key.DO,
                    (i - 1 to j) to path + Key.UP,
                    (i to j + 1) to path + Key.RI,
                    (i to j - 1) to path + Key.LE,
                )
                neighbours.forEach { idx, lst ->
                    if (numPadValidFields.contains(idx)) {
                        pathMap.putIfAbsent(idx, lst)
                    }
                }
            }
        }
        return pathMap.mapKeys { (k,_) -> numPad[k]!! }[to]!!
    }

    fun main() {
        getShortestPath(Num.BA, Num.B1).print()
    }
}

fun main() = D21().main()