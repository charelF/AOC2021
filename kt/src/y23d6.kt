fun y23d6() {
    // Time:      7  15   30
    // Distance:  9  40  200
    //
    // Time:        50     74     86     85
    // Distance:   242   1017   1691   1252

    fun recordBeats(time: Long, record: Long): Long {
        for (i in (0..time)) {
            val distance: Long = (time - i) * i
            if (distance > record) {
                return time - (2 * i) + 1
            }
        }
        return 0L
    }
    val rb2: (Pair<Int, Int>) -> Long = { (t, r) -> recordBeats(t.toLong(), r.toLong()) }

    println(listOf(7 to 9, 15 to 40, 30 to 200).map(rb2).reduce(Long::times))
    println(listOf(50 to 242, 74 to 1017, 86 to 1691, 85 to 1252).map(rb2).reduce(Long::times))
    println(recordBeats(71530, 940200))
    println(recordBeats(50748685, 242101716911252L))
}

//kotlinc y23d6.kt
//kotlin Y23d6Kt