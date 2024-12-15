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
            str.flatMapIndexed { j, ch ->
                when (ch) {
                    '#' -> listOf(Cell.WALL, Cell.WALL)
                    'O' -> listOf(Cell.BOX, Cell.BOX)
                    '.' -> listOf(Cell.EMPTY, Cell.EMPTY)
                    '@' -> { robot = i to j*2; listOf(Cell.EMPTY, Cell.EMPTY) }
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
            Cell.BOX -> {
                when(move) { // moving double wide boxes horizontal is easy
                    Move.RIGHT, Move.LEFT -> {
                        var after = nextPos(possibleMove, move)
                        while (true) {
                            after = nextPos(after, move)
                            when (warehouse[after]) {
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
                    Move.UP, Move.DOWN -> {
                        // given that we don't separate between left and right box to simplify the above algorithm
                        // we now have to find out if a box is a left or right box
                        val pair = getBoxPair(possibleMove)
                        val aBoxes = mutableListOf(pair)
                        aBoxes.addAll(getAffectedBoxes(pair, move))
//                        println("affected boxes: $aBoxes")
                        val didMove = moveAllBoxes(aBoxes, move)
//                        println("did move? $didMove")
                        if (didMove)  robot = possibleMove

//                        val boxes: MutableList<DualDual<Int>> = mutableListOf(pair)
//                        while (true) {
//
//                            // get all affected boxes
//                            // check if we can move each box one up|down and above|below it is only either box or empty
//                            // if yes do move
//                        }
                    }
                }
            }
        }
    }

    fun getAffectedBoxes(box: DualDual<Int>, move: Move): List<DualDual<Int>> {
        // get all affected boxes, ignore any other cells
        val row = box.first.first
        val nextRow = if (move == Move.UP) row - 1 else row + 1

        val affRight = nextRow to box.first.second
        val affLeft = nextRow to box.second.second
        val affectedBoxes = mutableSetOf<DualDual<Int>>()

        if (warehouse[affRight] == Cell.BOX) {
            val affBox = getBoxPair(affRight)
            affectedBoxes.add(affBox)
            affectedBoxes.addAll(getAffectedBoxes(affBox, move))
        }
        if (warehouse[affLeft] == Cell.BOX) {
            val affBox = getBoxPair(affLeft)
            affectedBoxes.add(affBox)
            affectedBoxes.addAll(getAffectedBoxes(affBox, move))
        }

        return affectedBoxes.toList()
    }

    fun moveAllBoxes(boxes: List<DualDual<Int>>, move: Move): Boolean {
//        println("about to move $boxes")
        val newY = if (move == Move.UP) - 1 else + 1
        // box = ([yx][yx])
        val newBoxes = boxes.map { box ->
            (box.first.first + newY to box.first.second) to (box.second.first + newY to box.second.second)
        }
//        println("towards $newBoxes")
        val cells = newBoxes.flatMap { (left, right) -> listOf(warehouse[left], warehouse[right]) }
//        println("cells: $cells, ${cells.contains(Cell.WALL)}")
        if (cells.contains(Cell.WALL)) return false // cant move, so do nothing
        else {
            // can move them
            // delete the current ones
            boxes.forEach { (left, right) ->
                warehouse[left] = Cell.EMPTY
                warehouse[right] = Cell.EMPTY
            }
            // redraw the moved ones
            newBoxes.forEach { (left, right) ->
                warehouse[left] = Cell.BOX
                warehouse[right] = Cell.BOX
            }
        }
        return true
    }

    fun getBoxPair(location: Dual<Int>): DualDual<Int> {
        val row = warehouse[location.first]
        val boxes = row.drop(location.second + 1).takeWhile { it == Cell.BOX }.count()  // no idea why +1
        val isRightBox = boxes % 2 == 0
        return if (isRightBox) (location.first to location.second-1) to location
        else location to (location.first to location.second+1)
    }

    fun gps(): Int {
        return warehouse.flatMapIndexed { i, rows ->
            rows.mapIndexed { j, cell ->
                if (cell == Cell.BOX) {
                    val (left, _) = getBoxPair(i to j)
                    100 * left.first + left.second
                } else 0
            }
        }.sum() / 2
    }

    fun print() {
        warehouse.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (robot == i to j) {
                    print("@")
                } else {
                    when (cell) {
                        Cell.EMPTY -> print(".")
                        Cell.BOX -> print("░")
                        Cell.WALL -> print("#")
                    }
                }
            }
            println()
        }
    }

    fun main() {
        print()
        while(moves.isNotEmpty()) {
//            println(moves)
            move()
//            print()
        }
        println(gps())
    }
}

fun main() = D15().main()

//
//package y2024
//
//import java.io.File
//import extensions.*
//
//fun <T> T.print() = println(this)
//
//class D15 {
//    enum class Cell { BOX, WALL, EMPTY }
//    enum class Move { UP, DOWN, RIGHT, LEFT }
//
//    val warehouse: MutableList<MutableList<Cell>>
//    val moves: MutableList<Move>
//    var robot = -1 to -1
//
//    private fun nextPos(pos: Dual<Int>, move: Move) = when(move) {
//        Move.UP -> pos.first - 1 to pos.second
//        Move.DOWN -> pos.first + 1 to pos.second
//        Move.RIGHT -> pos.first to pos.second + 1
//        Move.LEFT -> pos.first to pos.second - 1
//    }
//
//    init {
//        val (p1, p2) = File("../i24/15").readText().split("\n\n")
//        warehouse = p1.split("\n").mapIndexed { i, str ->
//            str.mapIndexed { j, ch ->
//                when (ch) {
//                    '#' -> Cell.WALL
//                    'O' -> Cell.BOX
//                    '.' -> Cell.EMPTY
//                    '@' -> { robot = i to j; Cell.EMPTY }
//                    else -> throw Exception("invalid object $ch")
//                }
//            }.toMutableList()
//        }.toMutableList()
//        moves = p2.replace("\n", "").map { ch ->
//            when (ch) {
//                '>' -> Move.RIGHT
//                '^' -> Move.UP
//                'v' -> Move.DOWN
//                '<' -> Move.LEFT
//                else -> throw Exception("invalid move >$ch<")
//            }
//        }.toMutableList()
//    }
//
//    fun move() {
//        val move = moves.removeFirst()
//        val possibleMove = nextPos(robot, move)
//        when (warehouse[possibleMove]) {
//            Cell.WALL -> return // do nothing
//            Cell.EMPTY -> robot = possibleMove  // move
//            Cell.BOX -> {  // the `move` made us run into the wall, check the next ones
//                var after = possibleMove
//                while (true) {
//                    after = nextPos(after, move)  // check whats behind
//                    when(warehouse[after]) {
//                        Cell.BOX -> continue
//                        Cell.WALL -> return // nothing we can do
//                        Cell.EMPTY -> {
//                            warehouse[after] = Cell.BOX  // put a box there (simulate move)
//                            robot = possibleMove // move
//                            warehouse[possibleMove] = Cell.EMPTY
//                            return
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun gps(): Int {
//        return warehouse.flatMapIndexed { i, rows ->
//            rows.mapIndexed { j, cell ->
//                if (cell == Cell.BOX) 100 * i + j else 0
//            }
//        }.sum()
//    }
//
//    fun print() {
//        warehouse.forEachIndexed { i, row ->
//            row.forEachIndexed { j, cell ->
//                if (robot == i to j) {
//                    print("@")
//                } else {
//                    when (cell) {
//                        Cell.EMPTY -> print(".")
//                        Cell.BOX -> print("O")
//                        Cell.WALL -> print("#")
//                    }
//                }
//            }
//            println()
//        }
//    }
//
//    fun main() {
//        while(moves.isNotEmpty()) move()
//        println(gps())
//    }
//}
//
//fun main() = D15().main()