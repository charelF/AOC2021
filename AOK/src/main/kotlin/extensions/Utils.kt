package extensions

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

typealias Dual<E> = Pair<E, E>
typealias DualDual<E> = Pair<Pair<E, E>, Pair<E, E>>

fun Boolean.toInt() = if (this) 1 else 0

operator fun Dual<Int>.plus(other: Dual<Int>): Dual<Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

fun Dual<Int>.abs(): Dual<Int> {
    return abs(this.first) to abs(this.second)
}

operator fun Dual<Int>.minus(other: Dual<Int>): Dual<Int> {
    return Pair(this.first - other.first, this.second - other.second)
}

operator fun Dual<Int>.times(other: Dual<Int>): Dual<Int> {
    return Pair(this.first * other.first, this.second * other.second)
}

operator fun Dual<Int>.times(number: Int): Dual<Int> {
    return Pair(this.first * number, this.second * number)
}

infix fun Dual<Int>.mod(other: Dual<Int>): Dual<Int> {
    return Pair(this.first % other.first, this.second % other.second)
}

fun Dual<Int>.isWithin(bounds: Dual<Int>): Boolean {
    return this.first >= 0 && this.first < bounds.first &&
            this.second >= 0 && this.second < bounds.second
}

enum class DistanceMetric {
    CHEBYSHEV, MANHATTAN
}

fun Dual<Int>.getNeighbours(metric: DistanceMetric, distance: Int = 1): Set<Dual<Int>> {
    // TODO: optimise this code maybe? into a sequence?
    val cells = mutableSetOf(this)
    if (distance == 0) return cells
    val newCells = mutableSetOf<Dual<Int>>()
    repeat(distance) {
        for (cell in cells) {
            newCells.add(cell.first-1 to cell.second)
            newCells.add(cell.first+1 to cell.second)
            newCells.add(cell.first to cell.second-1)
            newCells.add(cell.first to cell.second+1)
            if (metric == DistanceMetric.CHEBYSHEV) {
                newCells.add(cell.first-1 to cell.second-1)
                newCells.add(cell.first+1 to cell.second+1)
                newCells.add(cell.first+1 to cell.second-1)
                newCells.add(cell.first-1 to cell.second+1)
            }
        }
        cells.addAll(newCells)
        newCells.clear()
    }
    cells.remove(this)
    return cells
}

fun Dual<Int>.distanceFrom(other: Dual<Int>, metric: DistanceMetric): Int {
    return when (metric) {
        DistanceMetric.MANHATTAN -> (this - other).abs().sum()
        DistanceMetric.CHEBYSHEV -> TODO()
    }
}

fun Dual<Int>.sum(): Int {
    return this.first + this.second
}

operator fun <E> Iterable<Iterable<E>>.get(index: Dual<Int>): E {
    return this.elementAt(index.first).elementAt(index.second)
}

operator fun <E> MutableList<MutableList<E>>.set(index: Dual<Int>, value: E) {
    this[index.first][index.second] = value
}


fun <E> List<E>.pairwise(withSelf: Boolean): Sequence<Pair<E,E>> = sequence {
    for (i in this@pairwise.indices) {
        for (j in i until this@pairwise.size) {
            if (!withSelf && (i == j)) continue // skip pairs with same index
            yield(this@pairwise[i] to this@pairwise[j])
        }
    }
}

fun <T> MutableList<T>.swap(i1: Int, i2: Int){
    val tmp = this[i1]
    this[i1] = this[i2]
    this[i2] = tmp
}

fun <T> MutableList<T>.swap(i1: IntRange, i2: IntRange) {
    for (i in i1) {
        for (j in i2) {
            val tmp = this[i]
            this[i] = this[j]
            this[j] = tmp
        }
    }
}

enum class ChooseBy {
    REPETITION, UNIQUE, SET
}
fun <E> List<E>.combinations(
    by: ChooseBy,
    subSet: Int? = null,
): Sequence<List<E>> = sequence {
    // Algorithm from https://stackoverflow.com/a/17996834/9439097
    val n = subSet ?: this@combinations.size
    suspend fun SequenceScope<List<E>>.combinationsRecursive(soFar: List<E>, rest: List<E>, n: Int) {
        if (n == 0) {
            yield(soFar) // Base case: yield the current combination
        } else {
            for (i in rest.indices) {
                val nextRest = when(by) {
                    ChooseBy.SET -> rest.slice(i + 1 until rest.size)
                    ChooseBy.UNIQUE -> rest.filterIndexed { index, _ -> index != i }
                    ChooseBy.REPETITION -> rest
                }
                combinationsRecursive(soFar + rest[i], nextRest, n - 1) // Recursive call
            }
        }
    }
    combinationsRecursive(emptyList(), this@combinations, n)
}
// some quite weird things below with the scope. the actual function (if not wrapped) looks like this
//fun combination(withRepetition: Boolean, soFar: List<Int>, rest: List<Int>, n: Int): Sequence<List<Int>> = sequence {
//    if (n == 0) {
//        yield(soFar)
//    } else {
//        for (i in rest.indices) {
//            val nextRest = if (withRepetition) rest else rest.filterIndexed { index, _ -> index != i }
//            yieldAll(combination(withRepetition, soFar + rest[i], nextRest, n - 1))
//        }
//    }
//}

fun <T> T.print() = println(this)

fun <T> List<List<T>>.print2D(pad: Int, map: Map<T, String> = mapOf()): List<List<T>> {
    this.forEachIndexed { i, line ->
        line.forEachIndexed { j, t ->
            val v = map[t] ?: t.toString()
            print(v.padStart(pad, ' '))
        }
        println()
    }
    return this
}

fun Double.isCloseTo(value: Long, epsilon: Double = 0.0001): Boolean {
    return abs(this - value) < epsilon
}

infix fun Int.pow(x: Int): Int {
    return this.toDouble().pow(x).roundToInt()
}

infix fun Long.pow(x: Long): Long {
    return this.toDouble().pow(x.toDouble()).roundToLong()
}

fun String.splitAt(i: Int): Pair<String, String> {
    return this.take(i) to this.drop(i)
}

/**
 * computing 2^exp more efficiently via bitshifting
 */
fun pow2L(exp: Int): Long = 1L shl exp

fun pow2(exp: Int): Int = 1 shl exp


fun <E> Iterable<Iterable<E>>.dualIndexOf(predicate: (E) -> Boolean): Dual<Int> {
    return this.mapIndexedNotNull { i, row ->
        val j = row.indexOfFirst { predicate(it) }
        if (j == -1) null else i to j
    }.first()
}