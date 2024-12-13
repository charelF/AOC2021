package y2024

import extensions.*
import java.io.File


class D13 {
    val machines: List<Machine>

    data class Machine(
        val buttonA: Dual<Int>,
        val buttonB: Dual<Int>,
        val prize: Dual<Int>
    ) {
        val buttonX = buttonA.first to buttonB.first
        val buttonY = buttonA.second to buttonB.second
    }

    init {
        val regex = Regex("[+=](\\d+)")
        machines = File("i24/13").readText().split("\n\n").map { inputGroup ->
            val groups = inputGroup.split("\n").map { line  ->
                regex.findAll(line).toList().map { it.groupValues[1].toInt() }
            }
            Machine(
                buttonA = groups[0][0] to groups[0][1],
                buttonB = groups[1][0] to groups[1][1],
                prize = groups[2][0] to groups[2][1],
            )
        }
    }

    fun simulateAllPresses(machine: Machine): Int? {
        return (1 until 101).flatMap { i ->
            (1 until 101).mapNotNull { j ->
                if ((machine.buttonA * i) + (machine.buttonB * j) == machine.prize) 3*i + j else null
            }
        }.minOrNull()
    }

    fun main() {
        machines.mapNotNull {m -> simulateAllPresses(m)}.sum().also(::println)
    }

}