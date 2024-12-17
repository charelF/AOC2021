package y2024

import extensions.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.measureTimeMillis

class D17(
    var regA: Long,
    var regB: Long,
    var regC: Long,
    var program: List<Dual<Long>>
) {
    fun main(): MutableList<Long> {
        var opcode: Long
        var operand: Long
        var combo: Long
        var pointer = 0
        val output = mutableListOf<Long>()
        while(pointer < program.size) {
            val ops = program[pointer++]
            opcode = ops.first
            operand = ops.second
            combo = when (operand) {
                0L, 1L, 2L, 3L -> operand
                4L -> regA
                5L -> regB
                6L -> regC
                else -> return mutableListOf()
            }
            when (opcode) {
                0L -> { regA /= (2L pow combo) }
                1L -> { regB = regB xor operand }
                2L -> { regB = (combo % 8L) }
                3L -> { if (regA != 0L) pointer = operand.toInt()}
                4L -> { regB = regB xor regC }
                5L -> { output.add(combo % 8L) }
                6L -> { regB = regA / (2L pow combo) }
                7L -> { regC = regA / (2L pow combo) }
                else -> throw IllegalArgumentException("Illegal opcode")
            }
        }
        return output
    }
}

fun main() {
    // So, the program 0,1,2,3 would run the instruction whose opcode is 0 and pass it the operand 1,
    // then run the instruction having opcode 2 and pass it the operand 3, then halt.
    val text = File("../i24/17").readText()
    val findReg = {ch: Char -> Regex("Register $ch: (\\d+)").find(text)!!.groupValues.last().toLong() }
    val regA = findReg('A')
    val flatProgram = Regex("Program: ([\\d,]+)").find(text)!!.groupValues
        .last().split(",").map(String::toLong)
    val program = flatProgram.chunked(2).map { it.first() to it.last() }

    val d17 = D17(regA, 0,0, program)
    d17.main().print()
    println("---")

    val totalSize = Long.MAX_VALUE / 100000
    val batchSize = 1
    val stepSize = totalSize / 100
    println(flatProgram)
    measureTimeMillis {
        runBlocking {
            val jobs = (0L until totalSize step stepSize).map { i ->
                launch(Dispatchers.Default) {
                    repeat(batchSize) { j ->
                        val out = D17(i + j, 0, 0, program).main()
                        println("$out i $i | j $j ")
                        if (out == flatProgram) {
                            println("FOUND: $i")
                        }
                    }
                }
            }
        }
    }.print()
}

//fun tests() {
//    var d17 = D17()
//    d17.run(Triple(0, 0, 9), listOf(2 to 6))
//    println(d17.regA == 0)
//    println(d17.regB == 1)
//    println(d17.regC == 9)
//    println(d17.output.isEmpty())
//
//    d17 = D17()
//    d17.run(Triple(10,0,0), listOf(5 to 0, 5 to 1, 5 to 4))
//    println(d17.regA == 10)
//    println(d17.regB == 0)
//    println(d17.regC == 0)
//    println(d17.output == listOf(0,1,2))
//
//    d17 = D17()
//    d17.run(Triple(2024,0,0), listOf(0 to 1, 5 to 4, 3 to 0))
//    println(d17.regA == 0)
//    println(d17.regB == 0)
//    println(d17.regC == 0)
//    println(d17.output == listOf(4,2,5,6,7,7,7,7,3,1,0))
//
//    d17 = D17()
//    d17.run(Triple(0,29,0), listOf(1 to 7))
//    println(d17.regB == 26)
//    println(d17.output.isEmpty())
//
//    d17 = D17()
//    d17.run(Triple(0,2024,43690), listOf(4 to 0))
//    println(d17.regB == 44354)
//    println(d17.output.isEmpty())
//}