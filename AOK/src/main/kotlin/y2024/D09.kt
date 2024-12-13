package y2024

import extensions.swap
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime


class D09 {
    private fun f1(inp: List<Int?>): List<Int?> {
        val lst = inp.toMutableList()
        while (true) {
            val space = lst.indexOfFirst { it == null }
            val num = lst.indexOfLast { it != null }
            if (space > num) break
            lst.swap(space, num)
        }
        return lst
    }

    private fun f2(inp: List<Int?>): List<Int?> {
        val lst = inp.toMutableList()
        val lastFile = lst.filterNotNull().max()
        for (file in lastFile downTo 0) {
            val first = lst.indexOfFirst { it==file }!!
            val size = lst.count { it==file }  // possible optimisation: .slice(max(0, first-10) .. min(first+10, lst.lastIndex))
            val sourceRange = first until (first + size)
            for (i in 0 until lst.size - size) {
                val destRange = i until i + size
                if (sourceRange.first > destRange.first) {
                    if (lst.slice(destRange).all { it == null }) {
                        lst.swap(sourceRange, destRange)
                        break
                    }
                }
            }
        }
        return lst
    }

    private fun checkSum(lst: List<Int?>): Long {
        return lst.asSequence().mapIndexed {i, num ->
            if (num != null) i * num.toLong() else 0
        }.sum()
    }

    fun main() {
        val lst = mutableListOf<Int?>()
        measureTimeMillis {
            File("../i24/9").readText()
                .map { it.toString().toInt() }
                .forEachIndexed { i, num ->
                    repeat(num) { _ ->
                        if (i % 2 == 0) lst.add(i / 2)
                        else lst.add(null)
                    }
                }
        }.also { println("parsing $it ms") }
        measureTimeMillis {
            println(checkSum(f1(lst)) to checkSum(f2(lst)))
        }.also { println("computing $it ms") }

    }
}

fun main() { D09().main() }