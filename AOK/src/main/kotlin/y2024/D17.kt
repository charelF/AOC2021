package y2024

import extensions.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.measureTimeMillis

//550ms for 200k starting values of A
//fun run(intialA: Long, program: List<Dual<Int>>): MutableList<Long> {
//    var a = intialA
//    var b = 0L
//    var c = 0L
//    var opcode: Int
//    var operand: Int
//    var combo: Long
//    var pointer = 0
//    val output = mutableListOf<Long>()
//    while (pointer < program.size) {
//        val ops = program[pointer++]
//        opcode = ops.first
//        operand = ops.second
//        combo = when (operand) {
//            0, 1, 2, 3 -> operand.toLong()
//            4 -> a
//            5 -> b
//            6 -> c
//            else -> throw IllegalArgumentException("Illegal opcode")
//        }
//        when (opcode) {
//            0 -> a /= (2L pow combo)
//            1 -> b = b xor operand.toLong()
//            2 -> b = (combo % 8L)
//            3 -> if (a != 0L) pointer = operand
//            4 -> b = b xor c
//            5 -> output.add(combo % 8L)
//            6 -> b = a / (2L pow combo)
//            7 -> c = a / (2L pow combo)
//            else -> throw IllegalArgumentException("Illegal opcode")
//        }
//    }
//    return output
//}

//150ms for 200k starting values of A (99% due to `pow2`)
//fun run(intialA: Long, program: List<Dual<Int>>): MutableList<Long> {
//    var a = intialA
//    var b = 0L
//    var c = 0L
//    var opcode: Int
//    var operand: Int
//    var combo: Long
//    var pointer = 0
//    val output = mutableListOf<Long>()
//    while (pointer < program.size) {
//        val ops = program[pointer++]
//        opcode = ops.first
//        operand = ops.second
//
//        when {
//            opcode == 0 && operand == 4 -> a /= pow2L(a.toInt())  // wont happen too high
//            opcode == 0 && operand == 5 -> a /= pow2L(b.toInt())
//            opcode == 0 && operand == 6 -> a /= pow2L(c.toInt())
//            opcode == 0 -> a /= pow2L(operand)
//
//            opcode == 1 -> b = b xor operand.toLong()
//
//            opcode == 2 && operand == 4 -> b = (a % 8L)
//            opcode == 2 && operand == 5 -> b = (b % 8L)
//            opcode == 2 && operand == 6 -> b = (c % 8L)
//            opcode == 2 -> b = (operand % 8L)
//
//
//            opcode == 3 -> if (a != 0L) pointer = operand
//
//
//            opcode == 4 -> b = b xor c
//
//
//            opcode == 5 && operand == 4-> output.add(a % 8L)
//            opcode == 5 && operand == 5-> output.add(b % 8L)
//            opcode == 5 && operand == 6-> output.add(c % 8L)
//            opcode == 5 -> output.add(operand % 8L)
//
//
//            opcode == 6&& operand == 4 -> b = a / pow2L(a.toInt())
//            opcode == 6 && operand == 5-> b = a / pow2L(b.toInt())
//            opcode == 6 && operand == 6-> b = a / pow2L(c.toInt())
//            opcode == 6 -> b = a / pow2L(operand)
//
//            opcode == 7 && operand == 4-> c = a / pow2L(a.toInt())
//            opcode == 7&& operand == 5 -> c = a / pow2L(b.toInt())
//            opcode == 7 && operand == 6-> c = a / pow2L(c.toInt())
//            opcode == 7 -> c = a / pow2L(operand)
//
//            else -> throw IllegalArgumentException("unexpected")
//        }
//    }
//    return output
//}

//fun run(intialA: Long, program: List<Dual<Int>>): MutableList<Long> {
//    var a = intialA
//    var b = 0L
//    var c = 0L
//    var opcode: Int
//    var operand: Int
//    var combo: Long
//    var pointer = 0
//    val output = mutableListOf<Long>()
//    while (pointer < program.size) {
//        val ops = program[pointer++]
//        prev = current
//        current = ops
//
//        if (prev == 1 to 5 && current != 7 to 5) {
//            TODO()
//        }
//
//        opcode = ops.first
//        operand = ops.second
//        when {
//            opcode == 0 -> a /= 8
//            opcode == 1 -> b = b xor operand.toLong() // (1, 5) (1, 6)
//            opcode == 2 && operand == 4 -> b = (a % 8L)
//            opcode == 3 -> if (a != 0L) pointer = 0
//            opcode == 4 -> b = b xor c
//            opcode == 5 && operand == 5-> output.add(b % 8L)
//            opcode == 7 && operand == 5 -> c = a / pow2L(b.toInt())
//            else -> throw IllegalArgumentException("unexpected")
//        }
//    }
//    return output
//}

val memory : MutableMap<Dual<Int>, Int> = mutableMapOf()
//memory[ops] = memory.getOrDefault(ops, 0) + 1

var prev: Dual<Int> = -1 to -1
var current: Dual<Int> = -1 to -1

fun run(intialA: Long, program: List<Dual<Int>>): MutableList<Long> {
    var a = intialA
    var b = 0L
    var c = 0L
    var opcode: Int
    var operand: Int
    var combo: Long
    var pointer = 0
    val output = mutableListOf<Long>()
    while (pointer < program.size) {
        val ops = program[pointer++]
        prev = current
        current = ops

//        (5, 5)
//        (3, 0)
//        (2, 4)
//        (1, 5)
//        (7, 5)

        if (prev == 5 to 5 && current != 3 to 0) { TODO() }
        if (prev != 5 to 5 && current == 3 to 0) { TODO() }
        if (prev == 3 to 0 && current != 2 to 4) { TODO() }

//        if (prev != 3 to 0 && current == 2 to 4) { TODO() }

        if (prev != 2 to 4 && current == 1 to 5) { TODO() }
        if (prev != 2 to 4 && current == 1 to 5) { TODO() }
        if (prev != 1 to 5 && current == 7 to 5) { TODO() }
        if (prev != 1 to 5 && current == 7 to 5) { TODO() }


        opcode = ops.first
        operand = ops.second
        when {
            opcode == 0 -> a /= 8
            opcode == 1 -> b = b xor operand.toLong() // (1, 5) (1, 6)
            opcode == 2 && operand == 4 -> b = (a % 8L)
            opcode == 3 -> if (a != 0L) pointer = 0
            opcode == 4 -> b = b xor c
            opcode == 5 && operand == 5-> output.add(b % 8L)
            opcode == 7 && operand == 5 -> c = a / pow2L(b.toInt())
            else -> throw IllegalArgumentException("unexpected")
        }
    }
    return output
}


// - removing closures: changing 0L -> {a /= (2L pow combo)} into 0L -> a /= (2L pow combo): no big change
// - changing from long to int for the operand, opcode, list: no difference in speed
// - big speedup (from 550ms to 150ms): computing power  of two (2L pow combo) with bitshift instead:
//   fun pow2(exp: Long): Long = 1L shl exp.toInt()
// - pre-allocating the output as an array and then just overwriting the empty entries: no speedup
//   which makes sense since the output list is also very small (16 values). maybe it made a difference of 3-4ms
// - one thing i figured out: when opcode is 0, 6, 7, and operand is a/b/c, it is a very unlikely situation since a/b/c can be very high numbers
//   however at the same, 63 is the highest value x st 2**63 still fits into a long,
// so its very likely that situation never happens i.e. 2**a. b and c may be smaller idk but at least a is very big
// using when(x) {4 -> ....} seems to take the same time as when() {x==4 -> ...} and allows for more creative patterns
// i merged the two when statements into oen big one useing the above knowledge (i.e just doing the conditions via when() {cond1 -> , instead of in the when(cond)
// then i commented out all the ones where the program still ran without them
// seems these are the only combinations that exist: {(2, 4)=11542401, (1, 5)=11542401, (7, 5)=11542401, (1, 6)=11542401, (0, 3)=11542401, (4, 0)=11542401, (5, 5)=11542401, (3, 0)=11542401}
// using that we can remove lots of cases and hardcoded the values in others (e.g. only opcode 3 case is (3, 0) so it means the pointer is always set to 0)
// from printing it seems b and c are still big numbers sometimes
// once i had the statement simplified, i started looking into sequences, e.g. (1,5) is always succeeded by (7,5), its never one or the other on its own
// so we can combine those instructions
// I found that there is one sequence: (5, 5) (3, 0) (2, 4) (1, 5) (7, 5) and except for (2,4) (which can happen without preceeding (3,0)) they all seem to happen only inside the sequence


fun main() {
    // So, the program 0,1,2,3 would run the instruction whose opcode is 0 and pass it the operand 1,
    // then run the instruction having opcode 2 and pass it the operand 3, then halt.
    val text = File("../i24/17").readText()
    val findReg = { ch: Char -> Regex("Register $ch: (\\d+)").find(text)!!.groupValues.last().toLong() }
    val regA = findReg('A').print()
    val flatProgram = Regex("Program: ([\\d,]+)").find(text)!!.groupValues
        .last().split(",").map(String::toInt).print()
    val program = flatProgram.chunked(2).map { it.first() to it.last() }

    run(regA, program).print()
    measureTimeMillis {
        (100_100_100_000_000L until 100_100_100_200_000L).forEach {
            run(it.toLong(), program)
        }
    }.also { println("took $it ms") }
    measureTimeMillis {
        (1 until 1234567).sumOf {
            run(it.toLong(), program).first()
        }.print()
    }.also { println("took $it ms") }
    run(100_100_100_000_000L, program).print()
    println("---")

    println("""
--SOLUTION--
24847151
[2, 4, 1, 5, 7, 5, 1, 6, 0, 3, 4, 0, 5, 5, 3, 0]
[7, 3, 1, 3, 6, 3, 6, 0, 2]
took 141 ms
4243641
took 240 ms
[3, 2, 5, 5, 0, 4, 1, 1, 6, 5, 0, 3, 6, 3, 7, 1]
    """
    )

    println(memory)
}


//    val totalSize = Long.MAX_VALUE / 100000
//    val batchSize = 1
//    val stepSize = totalSize / 100
//    println(flatProgram)
//    measureTimeMillis {
//        runBlocking {
//            val jobs = (0L until totalSize step stepSize).map { i ->
//                launch(Dispatchers.Default) {
//                    repeat(batchSize) { j ->
//                        val out = D17(i + j, 0, 0, program).main()
//                        println("$out i $i | j $j ")
//                        if (out == flatProgram) {
//                            println("FOUND: $i")
//                        }
//                    }
//                }
//            }
//        }
//    }.print()


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