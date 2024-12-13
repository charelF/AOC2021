package y2024

import extensions.*
import java.io.File
import org.apache.commons.math3.linear.*
import kotlin.math.roundToLong

fun main() {
    D13().main()
}

class D13 {
    val machines: List<Machine>

    data class Machine(
        val buttonA: Dual<Long>,
        val buttonB: Dual<Long>,
        val price: Dual<Long>
    ) {
        val buttonX = buttonA.first to buttonB.first
        val buttonY = buttonA.second to buttonB.second
    }

    operator fun Dual<Long>.plus(other: Dual<Long>): Dual<Long> {
        return Pair(this.first + other.first, this.second + other.second)
    }

    operator fun Dual<Long>.times(number: Long): Dual<Long> {
        return Pair(this.first * number, this.second * number)
    }

    init {
        val regex = Regex("[+=](\\d+)")
        machines = File("../i24/13").readText().split("\n\n").map { inputGroup ->
            val groups = inputGroup.split("\n").map { line  ->
                regex.findAll(line).toList().map { it.groupValues[1].toLong() }
            }
            Machine(
                buttonA = groups[0][0] to groups[0][1],
                buttonB = groups[1][0] to groups[1][1],
                price = groups[2][0] to groups[2][1],
            )
        }
    }


    fun simulate100Presses(machine: Machine): Int? {
        return (1 until 101).flatMap { i ->
            (1 until 101).mapNotNull { j ->
                if ((machine.buttonA * i.toLong()) + (machine.buttonB * j.toLong()) == machine.price) 3*i + j else null
            }
        }.minOrNull()
    }

    fun solveSystem(machine: Machine): Long? {
        val matrix = Array2DRowRealMatrix(arrayOf(
            doubleArrayOf(machine.buttonA.first.toDouble(), machine.buttonB.first.toDouble()),  // coefficients of the first equation
            doubleArrayOf(machine.buttonA.second.toDouble(), machine.buttonB.second.toDouble())   // coefficients of the second equation
        ))
        val solutionVector = ArrayRealVector(doubleArrayOf(machine.price.first.toDouble(), machine.price.second.toDouble())) // Right-hand side of equations

        // Solve the system using LU Decomposition
        val solver = LUDecomposition(matrix).solver
        if (!solver.isNonSingular) return null // there is no solution

        val solution = solver.solve(solutionVector).toArray().toList() // solution vector X
        val (pAD, pBD) = solution
        val (pAL, pBL) = solution.map { it.roundToLong() }  // important to round to nearest, not up/down
        // assumption: there is always only 1 solution, if it's not integer then we can also discard it
        val epsilon = 0.01  // a bit of a hacky way to find only the single integer solution
        return if (pAD.isCloseTo(pAL, epsilon) && pBD.isCloseTo(pBL, 0.01)) 3 * pAL + pBL else null
    }

    fun main() {
        val s1v1 = machines.mapNotNull(::simulate100Presses).sum()
        val s1 = machines.mapNotNull(::solveSystem).sum()
        val s2 = machines.map { machine ->
            Machine (
                buttonA = machine.buttonA,
                buttonB = machine.buttonB,
                price = machine.price.first + 10000000000000 to machine.price.second + 10000000000000
            )
        }.mapNotNull(::solveSystem).sum()
        println(s1 to s2)
    }

}