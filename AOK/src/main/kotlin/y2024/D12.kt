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
                .flatMap { it.getNeighbours(DistanceMetric.MANHATTAN) }
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
                            coloredGarden[pair] = color  // color all neighbours the same and then at the end switch to new color
                        }
                    color++
                }
            }
        }
        return coloredGarden
    }

    fun isNeighbour(fencePlant1: DualDual<Int>, fencePlant2: DualDual<Int>): Boolean {
        // if both fence and plant are neighbours, then they are neighbours on one side
        val (f1, p1) = fencePlant1
        val (f2, p2) = fencePlant2
        return (f1.getNeighbours(DistanceMetric.MANHATTAN).contains(f2)
                && p1.getNeighbours(DistanceMetric.MANHATTAN).contains(p2))
    }

    fun combineSides(fences: List<DualDual<Int>>): List<List<DualDual<Int>>> {
        // a side = a collection of fences where both the fences and plants are neighbours
        // so we need to find for each fence the other fences in this collection
        // for each node: find other nodes in its collection
        // empty list of collections
        // go through each cell, then check if it has a neighbour in one of the collections
        // if not, add a new side
        // if yes, add it to that collection
        val sideLists: MutableList<MutableList<DualDual<Int>>> = mutableListOf()
        fences.forEach { fence ->
            run outer@{
                sideLists.forEach { sideList ->
                    sideList.forEach { side ->
                        if (isNeighbour(fence, side)) {
                            sideList.add(fence) // if neighbours, then we add to the sidelist and check next fence
                            return@outer
                        }
                    }
                }
                sideLists.add(mutableListOf(fence))
            }
        }
        return sideLists
    }

    fun main() {
        println(f(1) to f(2))
    }

    fun f(part: Int = 1): Int {
        val coloredGarden = colorGarden()
        return coloredGarden.flatten().distinct().sumOf { color ->
            val plantCoordinates = coloredGarden.flatMapIndexed { i, row ->
                row.mapIndexedNotNull { j, plant ->
                    if (plant == color) i to j else null
                }
            }
            val fences = plantCoordinates.flatMap { plant ->
                plant.getNeighbours(DistanceMetric.MANHATTAN)
                    .filter { !plantCoordinates.contains(it) }
                    .map { it to plant }  // we associate each fence to the plant it fences
            }
            val fenceCount = if (part == 1) fences.size else combineSides(fences).size
            plantCoordinates.size * fenceCount
        }
    }
}