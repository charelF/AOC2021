import y2024.d01
import java.io.File
import kotlin.math.abs

fun main() {
    val a0 = File("i24/2").readLines()
        .map { line -> line.split(" ").map { it.toInt() } }
        .map { line ->
            val a = line.windowed(2).map { (v1, v2) ->
                Pair(v1 - v2 in 1..3, v2 - v1 in 1..3)
            }.unzip().toList().map { list -> list.all{it} }
            if (a.max()) 1 else 0
        }.sum()

    val a1 = File("i24/2").readLines()
        .map { line -> line.split(" ").map { it.toInt() } }
        .sumOf { line ->
            println(line)
            val lineVs: MutableList<List<Int>> = mutableListOf()
            repeat(line.size) { i ->
                val x = line.toMutableList()
                x.removeAt(i)
                lineVs.add(x)
            }
            lineVs.add(line) // add the original; needs to be safe too
            val x = lineVs.flatMap { line2 ->
                print(line2)
                val a = line2.windowed(2).map { (v1, v2) ->
                    Pair(v1 - v2 in 1..3, v2 - v1 in 1..3)
                }.unzip().toList().map { list -> list.all { it } }
                println(a)
                a
            }
            val res = if (x.max()) 1 else 0
            println(res)
            res
        }
    println(a1)



}