package y2024

import extensions.*
import java.io.File

class D12 {
    fun main() {
        val result = File("i24/12sss").readLines().map { it.toList() }
        val plants = result.flatten().distinct()
        plants.sumOf { plant ->
            println(plant)
            val plantCoordinates = result.flatMapIndexed { i, chars ->
                chars.mapIndexedNotNull { j, ch ->
                    if (ch == plant) i to j else null
                }
            }
            println("plant coordinates $plantCoordinates")
            val fenceCoordinates = plantCoordinates
                .map { it.getNeighbours(CellDistanceMetric.MANHATTAN) }
//                .filter { !plantCoordinates.contains(it) }
//                .toSet() subtract plantCoordinates

            println("fence coordinates $fenceCoordinates")



//            println(fenceCoordinates)

//            println(plantCoordinates.size to fenceCoordinates.size)

            val price = plantCoordinates.size * fenceCoordinates.size
            println(price)
            price
        }.also(::println)
    }
}