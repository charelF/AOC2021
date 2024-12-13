package y2024

import java.io.File
import extensions.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class D06 {
    private val start: Dual<Int>
    private val guardMap: List<List<Field>>
    private val m: Int
    private val n: Int

    private fun nextDir(dir: Dual<Int>): Dual<Int> {
        return when (dir) {
            1 to 0 -> 0 to -1
            0 to -1 -> -1 to 0
            -1 to 0 -> 0 to 1
            0 to 1 -> 1 to 0
            else -> throw Exception("not possible")
        }
    }

    private enum class Field {
        Empty, Obstructed, Visited
    }

    init {
        var start: Dual<Int> = -1 to -1
        guardMap = File("../i24/6").readLines().mapIndexed { i, line ->
            line.mapIndexed { j, char ->
                when(char) {
                    '#' -> Field.Obstructed
                    '.' -> Field.Empty
                    '^' -> Field.Empty.also { start = i to j } // so nice
                    else -> throw Exception("not possible")
                }
            }
        }
        this.start = start
        m = guardMap.size
        n = guardMap[0].size
    }

    private fun f1(): Int {
        val map = guardMap.map { it.toMutableList() }.toMutableList()
        var dir = -1 to 0
        var pos = start
        while (true) {
            val nextPos = pos + dir
            if (nextPos.isWithin(m to n)) {
                when(map[nextPos.first][nextPos.second]) {
                    Field.Obstructed -> dir = nextDir(dir)
                    Field.Empty, Field.Visited -> {
                        pos = nextPos
                        map[pos.first to pos.second] = Field.Visited
                    }
                }
            } else break
        }
        return map.sumOf { row ->
            row.count {it == Field.Visited}
        }
    }

    private fun isPossibleIfObstructed(obstruction: Dual<Int>): Boolean {
        var pos = start
        var dir = -1 to 0
        // remember (pos,dir) combinations to be able to tell in a loop, as we are in a loop if we are on a field
        // that was visited and in the same destination as we were in when we visited it
        val posDirCombinations: MutableSet<DualDual<Int>> = mutableSetOf()

        // generate a copy of the map with an obstacle in the specific spot
        val map = guardMap.map { it.toMutableList() }.toMutableList()
        map[obstruction.first to obstruction.second] = Field.Obstructed

        while (true) {
            val posDir = pos to dir
            if (posDirCombinations.contains(posDir)) {
                return true
            }
            posDirCombinations.add(posDir)
            val nextPos = pos + dir
            if (nextPos.isWithin(m to n)) {
                when(map[nextPos.first to nextPos.second]) {
                    Field.Obstructed -> dir = nextDir(dir)
                    Field.Empty, Field.Visited -> {
                        pos = nextPos
                        map[pos.first to pos.second] = Field.Visited
                    }
                }
            } else break
        }
        return false
    }

    private fun simpleF2(): Int {
        // put object in every possible spot
        var loopCount = 0
        repeat(m) { i ->
            repeat(n) { j ->
                if (isPossibleIfObstructed(i to j)) loopCount += 1
            }
        }
        return loopCount
    }

    private fun fastF2(): Int  {
        return runBlocking {
            val jobs = (0..<m).flatMap { i ->
                (0..<n).map { j ->
                    async(Dispatchers.Default) {
                        isPossibleIfObstructed(i to j).toInt()
                    }
                }
            }
            jobs.awaitAll().sum()
        }
    }

    fun main() {
        println(f1())

        measureTimeMillis {
            println(simpleF2())
        }.let { println("simpleF2 took $it ms")}

        measureTimeMillis {
            println(fastF2())
        }.let { println("fastF2 took $it ms")}
    }
}

fun main() { D06().main() }