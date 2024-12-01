package y2024

import java.io.File
import kotlin.math.abs

fun d01() {
    val filename = "i24/1"
    val input = File(filename)
    val (left, right) = input.readLines()
        .map { it.split("   ") }
        .map { Pair(it[0].toInt(), it[1].toInt()) }
        .unzip()
    val a1 = left.sorted().zip(right.sorted()).sumOf { pair -> abs(pair.first - pair.second) }
    val a2 = left.sumOf { num -> num * right.count { it == num } }
    println(a1)
    println(a2)
}