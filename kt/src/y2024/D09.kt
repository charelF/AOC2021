package y2024

import extensions.swap
import java.io.File


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
            val size = lst.count { it==file }
            val first = lst.indexOfFirst { it==file }!!
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
        return lst.map { it?.toLong() }.mapIndexed {i, num ->
            if (num != null) i * num else 0
        }.sum()
    }

    fun main() {
        val lst = mutableListOf<Int?>()
        File("i24/9s").readText()
            .map { it.toString().toInt() }
            .forEachIndexed { i, num ->
                repeat(num) { _ ->
                    if (i%2==0) lst.add(i/2)
                    else lst.add(null)
                }
            }

        println(checkSum(f1(lst)) to checkSum(f2(lst)))
    }
}