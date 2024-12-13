package y2024

import extensions.*
import java.io.File

class D10 {
    private val topMap: List<List<Int>> = File("i24/10").readLines().map { line ->
        line.map { cell ->
            when (cell) {
                '.' -> -1
                else -> cell.digitToInt()
            }
        }
    }
    private val trailheads: List<Dual<Int>> = topMap.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, value ->
            if (value == 0) i to j else null
        }
    }
    private val m = topMap.size
    private val n = topMap.first().size

    fun f(collectionBuilder: () -> MutableCollection<Dual<Int>>): Int {
        return trailheads.sumOf { trailhead ->
            val cells = (0..9).associateWith { collectionBuilder() }
            cells[0]!!.add(trailhead)
            for (i in 0 until 9) {
                val neighbours = cells[i]!!.flatMap { cell ->
                    cell.getNeighbours(DistanceMetric.MANHATTAN).asSequence()
                        .filter { nb -> nb.isWithin(m to n) }
                        .filter { nb -> topMap[nb] == i + 1 }
                }
                cells[i + 1]!!.addAll(neighbours)
            }
            cells[9]!!.size
        }
    }

    fun main() {
        // absolutely beautiful how part 1 and part2 only differ by their data structure
        println(f { mutableSetOf() } to f { mutableListOf() })
    }
}