package y2024

import java.io.File
import kotlin.math.abs

class D01 {
    fun main() {
        val (left, right) = File("../i24/1").readLines()
            .map { it.split("   ") }
            .map { Pair(it[0].toInt(), it[1].toInt()) }
            .unzip()
        val a1 = left.sorted().zip(right.sorted()).sumOf { pair -> abs(pair.first - pair.second) }
        val a2 = left.sumOf { num -> num * right.count { it == num } }
        println(a1 to a2)
    }
}

fun main() { D01().main() }