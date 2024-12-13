package y2024

import java.io.File
import extensions.*
import kotlin.system.measureTimeMillis

class D11 {
    val stones = File("../i24/11")
        .readText().split(" ")
        .groupingBy { it }.eachCount() // group identical numbers by key = number, value = count
        .entries.associate { (k,v) -> k.toLong() to v.toLong() } // change both key and value to long

    fun main() { println(f(25).values.sum() to f(75).values.sum()) }

    fun blink(num: Long, amount: Long): Map<Long, Long> = when {
        num == 0L -> mapOf(1L to amount)
        num.toString().count() % 2 == 0 -> num.toString()
            .chunked( num.toString().count()/2)
            .map { it.toLong() }
            .groupingBy {it}.eachCount()
            .mapValues {(k,v) -> v*amount}
        else -> mapOf(num*2024 to amount)
    }

    fun f(n: Int): Map<Long, Long> {
        return (0 until n).fold(stones) { acc, _ ->
            acc.map { (k, v) -> blink(k, v) }
                .flatMap { it.entries } // flatten into a single list of map.entry items
                .groupingBy { it.key }  // group entries by key
                .fold(0L) { sum, entry -> sum + entry.value } // sum the values for each key
        }
        // the fold is equivalent to overwriting stones repeatedly
        // var stones = ...
        // repeat(...) {
        //   stones = x()
        // }
    }
}

fun main() = D11().main()