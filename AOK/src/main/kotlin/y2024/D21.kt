package y2024

import extensions.*
import java.io.File

class D21 {
    enum class Key { UP, DO, RI, LE, A }
    enum class Num { B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, A }

    val numbers = File("../i24/21").readLines().associateWith { line ->
        line.map { c ->
            when {
                c.isDigit() -> Num.entries.first { num -> num.ordinal == c.digitToInt() }
                else -> Num.A
            }
        }
    }

    val numPad: List<List<Num?>> = listOf(
        listOf(Num.B7, Num.B8, Num.B9),
        listOf(Num.B4, Num.B5, Num.B6),
        listOf(Num.B1, Num.B2, Num.B3),
        listOf(null,   Num.B0, Num.A),
    )
    val numPadValidFields = numPad.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, v ->
            if (v != null) i to j else null
        }
    }

    val keyPad: List<List<Key?>> = listOf(
        listOf(null, Key.UP, Key.A),
        listOf(Key.LE, Key.DO, Key.RI),
    )
    val keyPadValidFields = keyPad.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, v ->
            if (v != null) i to j else null
        }
    }

    /**
     * Difficulty: Two paths can be equally short, but the next robot in line may take longer to produce one than the other
     * E.g. DO DO LE == DO, LE, DO, but next robot has to move around more to enter the second path
     * so we need to find the optimal path, which is a straight as possible (avoid moves)
     * and also prefer directions in this way: LE, UP, DO, RI (idk why, got that part from reddit)
     */
    fun <T> getOptimalPaths(pad: List<List<T>>, pvf: List<Dual<Int>>, from: T): Map<T, List<Key>> {
        val startIdx = pad.dualIndexOf {it == from}
        val pathMap: MutableMap<Dual<Int>, List<Key>> = mutableMapOf(startIdx to listOf())
        repeat(5) {
            val snapshot = pathMap.toMap()
            for ((idx, path) in snapshot) {
                val (i, j) = idx
                val neighbours = mapOf<Dual<Int>, List<Key>>(
                    (i to j - 1) to path + Key.LE,
                    (i to j - 2) to path + Key.LE + Key.LE,
                    (i - 1 to j) to path + Key.UP,
                    (i - 2 to j) to path + Key.UP + Key.UP,
                    (i - 3 to j) to path + Key.UP + Key.UP + Key.UP,
                    (i + 1 to j) to path + Key.DO,
                    (i + 2 to j) to path + Key.DO + Key.DO,
                    (i + 3 to j) to path + Key.DO + Key.DO + Key.DO,
                    (i to j + 1) to path + Key.RI,
                    (i to j + 2) to path + Key.RI + Key.RI,
                )
                neighbours.forEach { idx, lst ->
                    if (pvf.contains(idx)) {
                        pathMap.putIfAbsent(idx, lst)
                    }
                }
            }
        }
        return pathMap.mapKeys { (k,_) -> pad[k]!! }
    }

    fun findMoves(number: List<Num>, robots: Int): Int {
        val seq1 = listOf(Num.A) + number
        var seq = seq1.windowed(2) { (v1, v2) -> getOptimalPaths(numPad, numPadValidFields, v1)[v2]!! + Key.A }

        repeat(robots) {
            seq = (listOf(Key.A) + seq.flatten()).windowed(2) { (v1, v2) ->
                getOptimalPaths(
                    keyPad,
                    keyPadValidFields,
                    v1
                )[v2]!! + Key.A
            }
        }
        return seq.flatten().size
    }


    // First optimisation: instead of recomputing the paths each time, store them in a map
    val optimalNumPadPathMap = Num.entries.flatMap { start ->
        val paths = getOptimalPaths(numPad, numPadValidFields, start)
        Num.entries.map { finish ->
            (start to finish) to paths[finish]!!
        }
    }.associate { (pair, path) -> pair to path }

    val optimalKeyPadPathMap = Key.entries.flatMap { start ->
        val paths = getOptimalPaths(keyPad, keyPadValidFields, start)
        Key.entries.map { finish ->
            (start to finish) to paths[finish]!!
        }
    }.associate { (pair, path) -> pair to path }

    fun findMoves2(number: List<Num>, robots: Int): Int {
        val seq1 = listOf(Num.A) + number
        var seq = seq1.windowed(2) { (f,l) -> optimalNumPadPathMap[f to l]!! + Key.A }

        repeat(robots) {
            seq = (listOf(Key.A) + seq.flatten()).windowed(2) { (f,l) -> optimalKeyPadPathMap[f to l]!! + Key.A }
        }
        return seq.flatten().size
    }

    // the list is way to big that we are able to store all produced sequences - instead we can just count
    // how many possible sequences there are (it seems 4 is the max of moves to enter any key)
    // and so we can just use a map that maps each sequence of 4 moves
    // to the sequence of sequence of moves that the next robot would need to enter

    // all combinations of 0-3 keys, ending with A
    val keyCombinations = (0..3).flatMap{ i ->
        Key.entries.dropLast(1).combinations(ChooseBy.REPETITION, i)
    }.map { it + Key.A }.toList()
    // produces [A], [UP, A], [DO, A], ..., [RI, LE, A], .... [UP, UP, UP, A] (4**3 + 4**2 + 4**1 + 4**0)

    // for each key-combination, this is the combination of keys to press to get this combination
    // e.g. [UP, A] requires [LE, A], [RI, A] (i think)
    val keyCombinationMap = keyCombinations.associate { lst ->
        lst to (listOf(Key.A) + lst).windowed(2) { (f,l) ->
            optimalKeyPadPathMap[f to l]!! + Key.A
        }
    }

    fun findMoves3(number: List<Num>, robots: Int): Long {
        val seq1 = listOf(Num.A) + number

        // run two normal ones first just to tackle the early edge case
        var seq = seq1.windowed(2) { (f,l) -> optimalNumPadPathMap[f to l]!! + Key.A }
        seq = (listOf(Key.A) + seq.flatten()).windowed(2) { (f,l) -> optimalKeyPadPathMap[f to l]!! + Key.A }

        // from now on we count instead of generating the list since its impossible to generate the full list for big inputs
        var seqCounter = seq.groupingBy {it}.eachCount().mapValues { it.value.toLong() }

        repeat(robots) {
            seqCounter = seqCounter.map { (se, count) ->
                keyCombinationMap[se]!! // first we expand each key-combination into the required key-combinations
                    .groupingBy { it }.eachCount() // we count them
                    .mapValues { it.value * count } // and then multiply them. So e.g. [UP, A] requires  [LE, A], [RI, A] then 100(count) [UP,A] require 100 [LE, A] and 100[RI,A]
            } // at this point we have list of maps, now we just need to transform it into a single map and sum all keys
                .flatMap { it.entries } // flatten into a single list of map.entry items
                .groupingBy { it.key }  // group entries by key
                .fold(0L) { sum, entry -> sum + entry.value } // sum the values for each key
        }
        return seqCounter.values.sum()
    }


    fun main() {
        // part 1
        numbers.map { (str, number) ->
            findMoves(number, 2) * str.dropLast(1).toInt()
        }.sum().print()
        numbers.map { (str, number) ->
            findMoves2(number, 2) * str.dropLast(1).toInt()
        }.sum().print()
        numbers.map { (str, number) ->
            findMoves3(number, 2) * str.dropLast(1).toInt()
        }.sum().print()

        // part 2
        numbers.map { (str, number) ->
            findMoves3(number, 25) * str.dropLast(1).toInt()
        }.sum().print()

    }
}

fun main() = D21().main()