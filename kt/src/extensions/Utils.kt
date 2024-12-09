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
