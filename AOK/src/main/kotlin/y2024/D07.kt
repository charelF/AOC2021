package y2024

import extensions.*
import java.io.File
import java.util.function.BinaryOperator

class D07 {
    enum class Op: BinaryOperator<Long> {
        PLUS { override fun apply(t: Long, u: Long): Long = t + u },
        MULT { override fun apply(t: Long, u: Long): Long = t * u },
        PIPE { override fun apply(t: Long, u: Long): Long = "$t$u".toLong() }
    }

    fun f(possibleOperations: List<Op>): Long {
        return File("../i24/7").readLines().sumOf { line ->
            val list = line.split(": ")
            val first = list[0].toLong()
            val nums = list[1].split(" ").map { it.toLong() }
            val isPossible = possibleOperations
                .combinations(ChooseBy.REPETITION, nums.size - 1)
                .map { combination ->
                    nums.reduceIndexed { i, acc, num ->
                        combination[i - 1].apply(acc, num)
                    }
                }
                .any { it == first }
            if (isPossible) first else 0
        }
    }

    fun main() {
        println(f(listOf(Op.PLUS, Op.MULT)) to f(Op.entries))
    }
}

fun main() { D07().main() }