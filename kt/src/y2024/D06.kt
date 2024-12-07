package y2024

import java.io.File
import extensions.*

enum class Field {
    Empty, Obstructed, Visited
}

class D06 {
    fun main() {
        var start: Pair<Int, Int> = -1 to -1

        val map = File("i24/6").readLines().mapIndexed { i, line ->
            line.mapIndexed { j, char ->
                when(char) {
                    '#' -> Field.Obstructed
                    '.' -> Field.Empty
                    '^' -> {
                        start = i to j
                        Field.Empty
                    }
                    else -> throw Exception("not possible")
                }
            }
        }
        val m = map.size
        val n = map[0].size

        val nextDir: (Pair<Int, Int>) -> Pair<Int, Int> = { current ->
            when (current) {
                1 to 0 -> 0 to -1
                0 to -1 -> -1 to 0
                -1 to 0 -> 0 to 1
                0 to 1 -> 1 to 0
                else -> throw Exception("not possible")
            }
        }

        // put object in every possible spot
        // check each field if it was visited, and when it was visited, if that was in the same direction
        var loopCount = 0
        repeat(map.size) { i ->
            println(i)
            repeat(map[0].size) { j ->
                var pos = start
                var dir = -1 to 0  // guard facing up at start

                val posDirCombinations: MutableSet<Pair<Pair<Int,Int>, Pair<Int, Int>>> = mutableSetOf()
                val newMap = map.map { it.toMutableList() }.toMutableList()
                newMap[i][j] = Field.Obstructed

                while (true) {
                    val posDir = pos to dir
                    if (posDirCombinations.contains(posDir)) {
                        loopCount++
                        break
                    }
                    posDirCombinations.add(posDir)

                    val nextPos = pos + dir
                    if (nextPos.isWithIn(m to n)) {
                        when(newMap[nextPos.first to nextPos.second]) {
                            Field.Obstructed -> dir = nextDir(dir)
                            Field.Empty, Field.Visited -> {
                                pos = nextPos
                                newMap[pos.first to pos.second] = Field.Visited
                            }
                            else -> throw Exception("not possible")
                        }
                    } else {
                        break
                    }
                }
            }
        }
//        val count = map.sumOf { row ->
//            row.count {it == Field.Visited}
//        }
//        println(count)
        println(loopCount)
    }
}



//package y2024
//
//import java.io.File
//
//enum class Field {
//    Empty, Obstructed, Visited
//}
//
//class D06 {
//    fun main() {
//        var pos: Pair<Int, Int> = -1 to -1
//        var dir = -1 to 0  // guard facing up at start
//
//        val map = File("i24/6s").readLines().mapIndexed { i, line ->
//            line.mapIndexed { j, char ->
//                when(char) {
//                    '#' -> Field.Obstructed
//                    '.' -> Field.Empty
//                    '^', '>', '<', 'v' -> {
//                        pos = i to j
//                        Field.Empty
//                    }
//                    else -> throw Exception("not possible")
//                }
//            }.toMutableList()
//        }.toMutableList()
//        println(map)
//
//        val isInMap: (Pair<Int, Int>) -> Boolean = { pair ->
//            (pair.first >= 0) && (pair.first < map.size) && (pair.second >= 0) && (pair.second < map[0].size)
//        }
//
//        operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
//            return Pair(this.first + other.first, this.second + other.second)
//        }
//
//        val nextDir: (Pair<Int, Int>) -> Pair<Int, Int> = { current ->
//            when (current) {
//                1 to 0 -> 0 to -1
//                0 to -1 -> -1 to 0
//                -1 to 0 -> 0 to 1
//                0 to 1 -> 1 to 0
//                else -> throw Exception("not possible")
//            }
//        }
//
//        while (true) {
//            val nextPos = pos + dir
//            if (isInMap(nextPos)) {
//                when(map[nextPos.first][nextPos.second]) {
//                    Field.Obstructed -> dir = nextDir(dir)
//                    Field.Empty, Field.Visited -> {
//                        pos = nextPos
//                        map[pos.first][pos.second] = Field.Visited
//                    }
//                    else -> throw Exception("not possible")
//                }
//            } else {
//                break
//            }
//        }
//        val count = map.sumOf { row ->
//            row.count {it == Field.Visited}
//        }
//        println(count)
//    }
//}