package y2024

import extensions.*
import java.io.File

class D17 {
    // So, the program 0,1,2,3 would run the instruction whose opcode is 0 and pass it the operand 1,
    // then run the instruction having opcode 2 and pass it the operand 3, then halt.
    val text = File("../i24/17s").readText()
    val findReg = {ch: Char -> Regex("Register $ch: (\\d+)").find(text)!!.groupValues.last().toInt() }
    var regA = findReg('A')
    var regB = findReg('B')
    var regC = findReg('C')
    val combos = Regex("Program: ([\\d,]+)").find(text)!!.groupValues
        .last().split(",").map(String::toInt).windowed(2,2).print()

    fun adv(v: Int) { regA = regA / (2 pow v) }
    fun bdv(v: Int) { regB = regA / (2 pow v) }
    fun cdv(v: Int) { regC = regA / (2 pow v) }

    fun main() {
        println(regA)

    }
}

fun main() = D17().main()