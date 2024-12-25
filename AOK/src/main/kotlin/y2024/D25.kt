package y2024

import java.io.File
import extensions.*

fun main() {
    File("../i24/25")
        .readText().split("\n\n")
        .map { it.lines() }
        .pairwise(withSelf = false)
        .sumOf { (block1, block2) ->
            block1.zip(block2).all { (row1, row2) ->
                row1.zip(row2).none { (char1, char2) ->
                    char1 == '#' && char2 == '#'
                }
            }.toInt()
        }.print()
}