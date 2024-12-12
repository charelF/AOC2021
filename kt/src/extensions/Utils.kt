package extensions

typealias Dual<E> = Pair<E, E>
typealias DualDual<E> = Pair<Pair<E, E>, Pair<E, E>>

fun Boolean.toInt() = if (this) 1 else 0

operator fun Dual<Int>.plus(other: Dual<Int>): Dual<Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

operator fun Dual<Int>.minus(other: Dual<Int>): Dual<Int> {
    return Pair(this.first - other.first, this.second - other.second)
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
    repeat(distance) {
        cells.forEach { cell ->
            cells.add(cell.first-1 to cell.second)
            cells.add(cell.first+1 to cell.second)
            cells.add(cell.first to cell.second-1)
            cells.add(cell.first to cell.second+1)
            if (metric == DistanceMetric.CHEBYSHEV) {
                cells.add(cell.first-1 to cell.second-1)
                cells.add(cell.first+1 to cell.second+1)
                cells.add(cell.first+1 to cell.second-1)
                cells.add(cell.first-1 to cell.second+1)
            }
        }
    }
    cells.remove(this)
    return cells
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




