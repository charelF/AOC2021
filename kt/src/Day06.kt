// input: i was too lazy to parse it
// Time:      7  15   30
// Distance:  9  40  200
//
// Time:        50     74     86     85
// Distance:   242   1017   1691   1252

fun recordBreakers(time: Long, record: Long): Long {
    for (i in (0..time)) {
        val distance: Long = (time - i) * i
        if (distance > record) {
            return time - (2 * i) + 1
        }
    }
    return 0L
}

// extension function for some unneeded syntactic sugar
fun List<Pair<Int, Int>>.recordBreakers(): List<Long> {
    return this.map { (t, r) ->
        recordBreakers(t.toLong(), r.toLong())
    }
}

fun day06() {
    println(listOf(7 to 9, 15 to 40, 30 to 200).recordBreakers().reduce(Long::times))
    println(listOf(50 to 242, 74 to 1017, 86 to 1691, 85 to 1252).recordBreakers().reduce(Long::times))
    println(recordBreakers(71530, 940200))
    println(recordBreakers(50748685L, 242101716911252L))
}