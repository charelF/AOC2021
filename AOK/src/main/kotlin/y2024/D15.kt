package y2024

import java.io.File
import extensions.*

fun <T> T.print() = println(this)

class D15 {
    enum class Cell { BOX, WALL, EMPTY }
    enum class Move { UP, DOWN, RIGHT, LEFT }

    val warehouse: MutableList<MutableList<Cell>>
    val moves: MutableList<Move>
    var robot = -1 to -1

    private fun nextPos(pos: Dual<Int>, move: Move) = when(move) {
        Move.UP -> pos.first - 1 to pos.second
        Move.DOWN -> pos.first + 1 to pos.second
        Move.RIGHT -> pos.first to pos.second + 1
        Move.LEFT -> pos.first to pos.second - 1
    }

    init {
        val (p1, p2) = File("../i24/15").readText().split("\n\n")
        warehouse = p1.split("\n").mapIndexed { i, str ->
            str.mapIndexed { j, ch ->
                when (ch) {
                    '#' -> Cell.WALL
                    'O' -> Cell.BOX
                    '.' -> Cell.EMPTY
                    '@' -> { robot = i to j; Cell.EMPTY }
                    else -> throw Exception("invalid object $ch")
                }
            }.toMutableList()
        }.toMutableList()
        moves = p2.replace("\n", "").map { ch ->
            when (ch) {
                '>' -> Move.RIGHT
                '^' -> Move.UP
                'v' -> Move.DOWN
                '<' -> Move.LEFT
                else -> throw Exception("invalid move >$ch<")
            }
        }.toMutableList()
    }

    fun move() {
        val move = moves.removeFirst()
        val possibleMove = nextPos(robot, move)
        when (warehouse[possibleMove]) {
            Cell.WALL -> return // do nothing
            Cell.EMPTY -> robot = possibleMove  // move
            Cell.BOX -> {  // the `move` made us run into the wall, check the next ones
                var after = possibleMove
                while (true) {
                    after = nextPos(after, move)  // check whats behind
                    when(warehouse[after]) {
                        Cell.BOX -> continue
                        Cell.WALL -> return // nothing we can do
                        Cell.EMPTY -> {
                            warehouse[after] = Cell.BOX  // put a box there (simulate move)
                            robot = possibleMove // move
                            warehouse[possibleMove] = Cell.EMPTY
                            return
                        }
                    }
                }
            }
        }
    }

    fun gps(): Int {
        return warehouse.flatMapIndexed { i, rows ->
            rows.mapIndexed { j, cell ->
                if (cell == Cell.BOX) 100 * i + j else 0
            }
        }.sum()
    }

    fun print() {
        warehouse.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (robot == i to j) {
                    print("@")
                } else {
                    when (cell) {
                        Cell.EMPTY -> print(".")
                        Cell.BOX -> print("O")
                        Cell.WALL -> print("#")
                    }
                }
            }
            println()
        }
    }

    fun main() {
        while(moves.isNotEmpty()) move()
        println(gps())
    }
}

fun main() = D15().main()