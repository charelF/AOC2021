package extensions

fun Boolean.toInt() = if (this) 1 else 0

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first - other.first, this.second - other.second)
}

fun Pair<Int, Int>.isWithIn(bounds: Pair<Int, Int>): Boolean {
    return this.first >= 0 && this.first < bounds.first &&
            this.second >= 0 && this.second < bounds.second
}

operator fun <E> Iterable<Iterable<E>>.get(index: Pair<Int, Int>): E {
    return this.elementAt(index.first).elementAt(index.second)
}

operator fun <E> MutableList<MutableList<E>>.set(index: Pair<Int, Int>, value: E) {
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

