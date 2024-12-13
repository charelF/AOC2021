package y2024

import extensions.toInt
import java.io.File

class D02 {
    fun main() {
        val input = File("../i24/2").readLines()
            .map { line -> line.split(" ").map { it.toInt() } }

        val a1 = input.sumOf { line ->
            line.windowed(2).map { (v1, v2) ->
                Pair(v1 - v2 in 1..3, v2 - v1 in 1..3)
            }.unzip().toList().any { list -> list.all { it } }.toInt()
        }

        val a2 = input.sumOf { line ->
            val lineVs: MutableList<List<Int>> = mutableListOf()
            repeat(line.size) { i ->
                val x = line.toMutableList()
                x.removeAt(i)
                lineVs.add(x)
            }
            lineVs.add(line) // add the original; needs to be safe too
            lineVs.flatMap { line2 ->
                line2.windowed(2).map { (v1, v2) ->
                    Pair(v1 - v2 in 1..3, v2 - v1 in 1..3)
                }.unzip().toList().map { list -> list.all { it } }
            }.max().toInt()
        }
        println(a1 to a2)
    }
}

fun main() { D02().main() }