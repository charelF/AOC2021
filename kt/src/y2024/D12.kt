package y2024

import extensions.*
import java.io.File

class D12 {
    val garden = File("i24/12").readLines().map { it.toList() }
    val m = garden.size
    val n = garden[0].size

    //    // todo : make this an extension function taking a filter as input
    fun getAllNeighbours(start: Dual<Int>, constraint: (Dual<Int>) -> Boolean): Set<Dual<Int>> {
        val cells = mutableSetOf(start)
        while (true) {
            val newCells = cells
                .flatMap { it.getNeighbours(CellDistanceMetric.MANHATTAN) }
                .filter(constraint)
                .subtract(cells)
            cells
            if (newCells.isEmpty()) break else cells.addAll(newCells)
        }
        return cells
    }

    fun colorGarden(): List<List<Int>> {
        val coloredGarden = (0 until m).map { (0 until n).map { i -> -1 }.toMutableList() }.toMutableList()
        var color = 1
        for (i in 0 until m) {
            for (j in 0 until n) {
                if (coloredGarden[i to j] == -1) {
                    getAllNeighbours(i to j) { it.isWithin(m to n) && garden[it] == garden[i to j] }
                        .forEach { pair ->
                            coloredGarden[pair] =
                                color  // color all neighbours the same and then at the end switch to new color
                        }.also { color++ }
                }
            }
        }
        return coloredGarden
    }

    fun main() {
        val coloredGarden = colorGarden()
        coloredGarden.flatten().distinct().sumOf { color ->
            println("color $color")
            val coordinates = coloredGarden.mapIndexedNotNull { i, row ->
                row.mapIndexedNotNull { j, cell ->
                    if (cell == color) i to j else null
                }.let { if (it.isEmpty()) null else it }
            }.flatten().distinct()
            val neighbours = coordinates.flatMap { coo ->
                coo.getNeighbours(CellDistanceMetric.MANHATTAN)
                    .filter { !coordinates.contains(it) }
                    .also(::println)
                    .map { nb -> nb to coo }
            }.distinct()
            neighbours.size * coordinates.size
        }.also(::println)
    }
}