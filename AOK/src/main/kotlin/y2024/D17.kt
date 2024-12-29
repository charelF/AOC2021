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


// then i commented out all the ones where the program still ran without them
// seems these are the only combinations that exist: {(2, 4)=11542401, (1, 5)=11542401, (7, 5)=11542401, (1, 6)=11542401, (0, 3)=11542401, (4, 0)=11542401, (5, 5)=11542401, (3, 0)=11542401}
// using that we can remove lots of cases and hardcoded the values in others (e.g. only opcode 3 case is (3, 0) so it means the pointer is always set to 0)

//when {
//            opcode == 0 -> a /= 8
//            opcode == 1 -> b = b xor operand.toLong() // (1, 5) (1, 6)
//            opcode == 2 && operand == 4 -> b = (a % 8L)
//            opcode == 3 -> if (a != 0L) pointer = 0
//            opcode == 4 -> b = b xor c
//            opcode == 5 && operand == 5-> output.add(b % 8L)
//            opcode == 7 && operand == 5 -> c = a / pow2L(b.toInt())
//            else -> throw IllegalArgumentException("unexpected")
//        }

// from printing it seems b and c are still big numbers sometimes
// once i had the statement simplified, i started looking into sequences, e.g. (1,5) is always succeeded by (7,5), its never one or the other on its own
// so we can combine those instructions
// I found that there is one sequence: (5, 5) (3, 0) (2, 4) (1, 5) (7, 5) and except for (2,4) (which can happen without preceeding (3,0)) they all seem to happen only inside the sequence
// except at the beginning e.g. my program is [2, 4, 1, 5, 7, 5, 1, 6, which means there is a 1,5 happening without the other elements
// so at some point the sequence starts

//when {
//    opcode == 0 -> a /= 8
//    opcode == 1 && operand == 6 -> b = b xor 6L
//    opcode == 4 -> b = b xor c
//
//    opcode == 5 && operand == 5-> {
//        while (pointer < program.size) {
//            output.add(b % 8L)
//            if (a != 0L) pointer = 0
//            b = (a % 8L) xor 5L  //(2,4)
//            c = a / pow2L(b.toInt())
//            b = b xor 6L
//            a /= 8
//            b = b xor c
//            pointer += 6
//        }
//        break
//    }

// the seuqence is actually longer than i assume, basically once we see the first (5,5) we will cycle only, each cycle
// now we just need to find what needs to happen such that we enter the (5,5) cycle:
// in my case, my input has a 5,5, (and no pointer moving before) so we know already exactly what happens before 5,5
// we can hardcode the input sequence and then later just loop

// runs in 30 ms
//fun run(intialA: Long, program: List<Dual<Int>>): MutableList<Long> {
//    var a = intialA
//    var b = 0L
//    var c = 0L
//    var pointer = 0
//    val output = mutableListOf<Long>()
//
//    b = (a % 8L)  // (2,4)
//    b = b xor 5L  // (1, 5)
//    c = a / pow2L(b.toInt())  // (7,5)
//    b = b xor 6L
//    a /= 8
//    b = b xor c
//
//    while (pointer < program.size) {
//        output.add(b % 8L)
//        if (a != 0L) pointer = 0
//        b = (a % 8L) xor 5L  //(2,4)
//        c = a / pow2L(b.toInt())
//        b = b xor 6L
//        a /= 8
//        b = b xor c
//        pointer += 6
//    }
//
//    return output
//}

// the loop starts even earlier and we can further simplify (removing the last 3 statements in the initialisaiton) and put them in the loop
// actually there is no inialisation, its just one big loop!

//fun run(intialA: Long, program: List<Dual<Int>>): MutableList<Long> {
//    var a = intialA
//    var b = 0L
//    var c = 0L
//    var pointer = 0
//    val output = mutableListOf<Long>()
//
//    while (pointer < program.size) {
//        b = (a % 8L) xor 5L
//        c = a / pow2L(b.toInt())
//        b = b xor 6L
//        a /= 8
//        b = b xor c
//        output.add(b % 8L)
//        if (a != 0L) pointer = 0
//        pointer += 6
//    }
//
//    return output
//}

// we can simplify more since  (a xor b) % x == (a%x) xor (b%x) this way we avoid casting b
// kind of hit a block again.. not sure how to continue
// b is an Int, its always between 0 and 8
// a and c can be arbitrary it seems (both depend on initial a)
// ok found something: if the pointer is not reset at the end, for my input of length 8, it will just do two more computations
// but thats not enough to reach the one where something is added to the output (i hope) so we can just break out
// and this way we can remove the whole pointer logic
// if (a != 0L) pointer = 6 else break

//fun run(intialA: Long): List<Int> {
//    var a = intialA
//    var b = 0
//    var c = 0L
//    var out: Int
//    val output = mutableListOf<Int>()
//    while (a != 0L) {
//        b = (a % 8L).toInt() xor 5
//        c = a / pow2L(b)
//        a = a / 8
//        out = ((b xor 6) % 8) xor (c%8L).toInt()
//        output.add(out)
//    }
//    return output
//}
//
//fun main() {
//    // So, the program 0,1,2,3 would run the instruction whose opcode is 0 and pass it the operand 1,
//    // then run the instruction having opcode 2 and pass it the operand 3, then halt.
//    val text = File("../i24/17").readText()
//    val findReg = { ch: Char -> Regex("Register $ch: (\\d+)").find(text)!!.groupValues.last().toLong() }
//    val regA = findReg('A').print()
//    val flatProgram = Regex("Program: ([\\d,]+)").find(text)!!.groupValues
//        .last().split(",").map(String::toInt).print()
//    val program = flatProgram.chunked(2).map { it.first() to it.last() }
//
//    println("input: $flatProgram")
//    run(regA, program).also { println("output: $it") }
//
//    measureTimeMillis {
//        (100_100_100_000_000L until 100_100_100_200_000L).forEach {
//            run(it.toLong(), program)
//        }
//    }.also { println("took $it ms") }
//    measureTimeMillis {
//        (1 until 1234567).sumOf {
//            run(it.toLong(), program).first()
//        }.print()
//    }.also { println("took $it ms") }
//    run(100_100_100_000_000L, program).print()
//    println("---")
//
//    println(
//        """
//--SOLUTION--
//24847151
//[2, 4, 1, 5, 7, 5, 1, 6, 0, 3, 4, 0, 5, 5, 3, 0]
//[7, 3, 1, 3, 6, 3, 6, 0, 2]
//took 141 ms
//4243641
//took 240 ms
//[3, 2, 5, 5, 0, 4, 1, 1, 6, 5, 0, 3, 6, 3, 7, 1]
//    """
//    )
//}

// found something: dividing a number by a power of 2 --> in binary its equivalent to just shifting right
// so a / pow2L(b) becomes a shr b
// another find (via chatgtp): we can modify (a % 8) to (a & 7)
// makes sense since 8 = 1000, mod8 = taking 1[000], 7 = 111, and e.g. 5 = 101, 5and 7 = 101 & 111 = 101
// also division by 8 = right shift by 3
// (re)using a mutable array instead of generating a list took the runtime down from 30ms to 10ms

//val arrayProgram = flatProgram.toIntArray()
//val arrayOut = IntArray(size = flatProgram.size) { 0 }
//fun run(startA: Long, out: IntArray) {
//    var a = startA
//    var b = 0
//    var c = 0L
//    var i = 0
//    while (a != 0L) {
//        b = (a and 7L).toInt() xor 5
//        c = a shr b
//        a = a shr 3
//        out[i++] = ((b xor 6) % 8) xor (c%8L).toInt()
//    }
//}

// can even remove c and could remove b
//fun run(startA: Long, out: IntArray) {
//    var a = startA
//    var b = 0
//    var i = 0
//    while (a != 0L) {
//        b = (a and 7L).toInt() xor 5
//        out[i++] = ((b xor 6) % 8) xor ((a shr b) % 8L).toInt()
//        a = a shr 3
//    }
//}
// we shift a right by 3 each round
// so that means to produce an output of length 8
// we need an input a of 3*16 = 48 bits or something like that
// now im seeing patterns run(pow2L(47), arrayOut) -> [3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 5]
// pow2L(45) is the first value of A where input length is 16, pow2L(45)-1 is not the case anymore
// similarly, pow2L(48) -1 is the last one that works, pow2L(48) overflows (makes sense to due unsafe i++)
// unfortunately, there are still 246290604621824 values between those two bounds ...
// we can move the pattern around: run(pow2L(48)-pow2L(20), arrayOut) == [3, 3, 3, 3, 3, 4, 1, 3, 3, 3, 3, 3, 3, 3, 3, 5]
// also my input has a trailing 0 so we can go down a bit
// we can simplify more: b is not needed
// this makes it visible that each outputed number only depends on 3 bits of a, they dont depend on each other
// a is not overwritten, just consumed from
// the output is deduced from a alone
// there are patterns
// (200, [5, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(208, [5, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(216, [5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(224, [4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(232, [4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(240, [4, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(248, [4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(256, [3, 2, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//(264, [3, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
//for (ia in 0..8*8*8 step 8) {
//    arrayOut = IntArray(size = flatProgram.size) { 0 }
//    run(ia.toLong(), arrayOut)
//    println(ia to arrayOut.toList())
////        println("ia $ia ${ia shr 3} ${ia shr 6}")
//}

//fun run(startA: Long, out: IntArray) {
//    var a = startA
//    var i = 0
//    while (a != 0L) {
//        out[i++] = ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
//        a = a shr 3
//    }
//}

// still looking for patterns - at least its fast - 1second for 100M entries

//measureTimeMillis {
//    var a = 0L
//    var i = 0
//    for (v in 0 until 100_000_000L) {
//        i = 0
//        a = start + v
//        while (a != 0L) {
//            arrayOut[i++] =
//                ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
//            a = a shr 3
//        }
////        println(arrayOut.toList())
////        break
//        if (arrayOut.take(7) == arrayProgram.take(7)) {
//            println(v)
//            println(v.toString(2))
//            println(v.toString(8))
//            println("---")
//        }
//        arrayOut.fill(0)
//    }
//}.print()

// i fiured out that we the last 8 digits can be exactly foudn like this:
//val start = pow2L(42)
//val end = pow2L(48) - 1
//var x = 1
//measureTimeMillis {
//    var a = 0L
//    var i = 0
//    for (v1 in start until end step pow2L(25)) {
// and the first 8 found can be similarly found but from the increasign from the front!

//val start = pow2L(42)
//val end = pow2L(48) - 1
//var x = 1
//measureTimeMillis {
//    var a = 0L
//    var i = 0
//    for (v1 in start until end step pow2L(25)) {
//        i = 0
//        a = v1
//        while (a != 0L) {
//            arrayOut[i++] =
//                ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
//            a = a shr 3
//        }
//        if (arrayOut.takeLast(8) == arrayProgram.takeLast(8)) {
//            println("${v1.toString(2)} ${arrayOut.toList()}")
//            break
//        }
//        arrayOut.fill(0)
//    }
//}

// i was able to brute force it by first finding which number to look for to match the last 8 values of the program
// those are very big numbers, but also with a very big stepsize so the seach space is reasonable (multiple million only)
// then once i find one, i search for all values up to the stepsize to find which numbers affect the first 8 values of the program
// this search space is also reasonable, multiple million only.
// final algorithm:

//val start = pow2L(42)
//val end = pow2L(48) - 1
//val stepsize = pow2L(24)
//measureTimeMillis {
//    var a = 0L
//    var i = 0
//    for (v1 in start until end step stepsize) {
//        i = 0
//        a = v1
//        while (a != 0L) {
//            arrayOut[i++] =
//                ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
//            a = a shr 3
//        }
//        if (arrayOut.takeLast(8) == arrayProgram.takeLast(8)) {
//            println("${v1.toString(2)} ${arrayOut.toList()}")
//
//            for (v2 in 0 until stepsize) {
//                i = 0
//                a = v1 + v2
//                while (a != 0L) {
//                    arrayOut[i++] =
//                        ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
//                    a = a shr 3
//                }
//                if (arrayOut.take(8) == arrayProgram.take(8)) {
//                    println("${v1.toString(2)} ${arrayOut.toList()}")
//                    println("found!")
//                    println(v1 + v2)
//                    return
//                }
//                arrayOut.fill(0)
//            }
//        }
//        arrayOut.fill(0)
//    }
//}


fun main() {
    // So, the program 0,1,2,3 would run the instruction whose opcode is 0 and pass it the operand 1,
    // then run the instruction having opcode 2 and pass it the operand 3, then halt.
    val text = File("../i24/17").readText()
    val findReg = { ch: Char -> Regex("Register $ch: (\\d+)").find(text)!!.groupValues.last().toLong() }
    val regA = findReg('A')
    val flatProgram = Regex("Program: ([\\d,]+)").find(text)!!.groupValues
        .last().split(",").map(String::toInt)
    val program = flatProgram.chunked(2).map { it.first() to it.last() }

    val arrayProgram = flatProgram.toIntArray()
    val arrayOut = IntArray(size = flatProgram.size) { 0 }
    println(arrayProgram.toList())
    println(arrayOut.toList())

    val start = pow2L(42)
    val end = pow2L(48) - 1
    val stepsize = pow2L(24)
    measureTimeMillis {
        var a = 0L
        var i = 0
        for (v1 in start until end step stepsize) {
            i = 0
            a = v1
            while (a != 0L) {
                arrayOut[i++] =
                    ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
                a = a shr 3
            }
            if (arrayOut.takeLast(8) == arrayProgram.takeLast(8)) {
                println("${v1.toString(2)} ${arrayOut.toList()}")

                for (v2 in 0 until stepsize) {
                    i = 0
                    a = v1 + v2
                    while (a != 0L) {
                        arrayOut[i++] =
                            ((((a and 7L).toInt() xor 5) xor 6) % 8) xor ((a shr ((a and 7L).toInt() xor 5)) % 8L).toInt()
                        a = a shr 3
                    }
                    if (arrayOut.take(8) == arrayProgram.take(8)) {
                        println("${v1.toString(2)} ${arrayOut.toList()}")
                        println("found!")
                        println(v1 + v2)
                        return
                    }
                    arrayOut.fill(0)
                }
            }
            arrayOut.fill(0)
        }
    }
}






//    arrayOut = IntArray(size = flatProgram.size) { 0 }
//    println(startBinary.chunked(8))

//    for (ia in 0b111111000-1 until 0b111111111+2) {
//
//
////        if (arrayOut.take(3).all { it == 3 } && arrayOut.slice(3 until 6) == arrayProgram.slice(3 until 6)) {
//
//        println(Integer.toBinaryString(ia.toInt()))
////            break
//
//    }

//    run(3287378L + 777L, arrayOut)  // doesnt work out
//    println(arrayOut.toList())


//    (777, [2, 4, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 2, 0])
//    (3287378, [3, 3, 3, 5, 7, 5, 1, 2, 3, 3, 3, 3, 1, 3, 2, 0])



//    (pow2L(41)).print()

//    run(regA, arrayOut)
    // [42, 45) is the range
//    run(pow2L(42), arrayOut)


//    for (ia in 0..8*8*8 step 8) {
//
//
//
//        println("ia $ia ${ia shr 3} ${ia shr 6}")
//    }






//    measureTimeMillis {
//        (100_000_000 until 100_200_000).forEach {
//            run(it.toLong(), arrayOut)
//        }
//    }.also { println("200k took $it ms") }


//    measureTimeMillis {
//        (1 until 1234567).sumOf {
//            run(it.toLong())[0]
//        }.print()
//    }.also { println("took $it ms") }
//    run(100_100_100_000_000L).print()
//    println("---")
//
//    println(
//        """
//--SOLUTION--
//24847151
//[2, 4, 1, 5, 7, 5, 1, 6, 0, 3, 4, 0, 5, 5, 3, 0]
//[7, 3, 1, 3, 6, 3, 6, 0, 2]
//took 141 ms
w//4243641
//took 240 ms
//[3, 2, 5, 5, 0, 4, 1, 1, 6, 5, 0, 3, 6, 3, 7, 1]
//    """
//    )
//}



//    println("input: $flatProgram")
//    run(regA, program).also { println("output: $it")  }


//    println(memory)



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