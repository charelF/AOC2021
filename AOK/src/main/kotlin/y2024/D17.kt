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

    fun adv0(op: Int) { regA = regA / (2 pow op) }
    fun bxl1(op: Int) { regB = regB xor op }
    fun bst2(op: Int) { regB = op % 8 }
    fun jnz3(op: Int) { if (regA != 0) instrPtr = op else instrPtr += 2 }
    fun bxc4(op: Int) { regB = regB xor regC } // Operand is ignored
    fun out5(op: Int) { output(op % 8) }
    fun bdv6(op: Int) { regB = regA / (2 pow op) }
    fun cdv7(op: Int) { regC = regA / (2 pow op) }

    fun main() {
        println(regA)

    }
}

fun main() = D17().main()