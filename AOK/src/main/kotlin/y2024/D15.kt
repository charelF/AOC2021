package y2024

import java.io.File
import extensions.*

fun <T> T.print() = println(this)

enum class Cell15 { BOX, WALL, EMPTY }
enum class Move15 { UP, DOWN, RIGHT, LEFT }

interface D15I {

    val warehouse: MutableList<MutableList<Cell15>>
    val moves: MutableList<Move15>
    var robot: Dual<Int>

    fun nextPos(pos: Dual<Int>, move: Move15) = when(move) {
        Move15.UP -> pos.first - 1 to pos.second
        Move15.DOWN -> pos.first + 1 to pos.second
        Move15.RIGHT -> pos.first to pos.second + 1
        Move15.LEFT -> pos.first to pos.second - 1
    }

    fun print() {
        warehouse.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (robot == i to j) {
                    print("@")
                } else {
                    when (cell) {
                        Cell15.EMPTY -> print(".")
                        Cell15.BOX -> print("O")
                        Cell15.WALL -> print("#")
                    }
                }
            }
            println()
        }
    }

    fun move()
    fun gps(): Int
    fun run(): Int {
        while(moves.isNotEmpty()) move()
        return gps()
    }
}

class D152: D15I {
    override val warehouse: MutableList<MutableList<Cell15>>
    override val moves: MutableList<Move15>
    override var robot = -1 to -1

    init {
        val (p1, p2) = File("../i24/15").readText().split("\n\n")
        warehouse = p1.split("\n").mapIndexed { i, str ->
            str.flatMapIndexed { j, ch ->
                when (ch) {
                    '#' -> listOf(Cell15.WALL, Cell15.WALL)
                    'O' -> listOf(Cell15.BOX, Cell15.BOX)
                    '.' -> listOf(Cell15.EMPTY, Cell15.EMPTY)
                    '@' -> { robot = i to j*2; listOf(Cell15.EMPTY, Cell15.EMPTY) }
                    else -> throw Exception("invalid object $ch")
                }
            }.toMutableList()
        }.toMutableList()
        moves = p2.replace("\n", "").map { ch ->
            when (ch) {
                '>' -> Move15.RIGHT
                '^' -> Move15.UP
                'v' -> Move15.DOWN
                '<' -> Move15.LEFT
                else -> throw Exception("invalid move >$ch<")
            }
        }.toMutableList()
    }

    override fun move() {
        val move = moves.removeFirst()
        val possibleMove = nextPos(robot, move)
        when (warehouse[possibleMove]) {
            Cell15.WALL -> return // do nothing
            Cell15.EMPTY -> robot = possibleMove  // move
            Cell15.BOX -> {
                when(move) { // moving double wide boxes horizontal is easy
                    Move15.RIGHT, Move15.LEFT -> {
                        var after = nextPos(possibleMove, move)
                        while (true) {
                            after = nextPos(after, move)
                            when (warehouse[after]) {
                                Cell15.BOX -> continue
                                Cell15.WALL -> return // nothing we can do
                                Cell15.EMPTY -> {
                                    warehouse[after] = Cell15.BOX  // put a box there (simulate move)
                                    robot = possibleMove // move
                                    warehouse[possibleMove] = Cell15.EMPTY
                                    return
                                }
                            }
                        }
                    }
                    Move15.UP, Move15.DOWN -> {
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

    private fun getAffectedBoxes(box: DualDual<Int>, move: Move15): List<DualDual<Int>> {
        // get all affected boxes, ignore any other cells
        val row = box.first.first
        val nextRow = if (move == Move15.UP) row - 1 else row + 1

        val affRight = nextRow to box.first.second
        val affLeft = nextRow to box.second.second
        val affectedBoxes = mutableSetOf<DualDual<Int>>()

        if (warehouse[affRight] == Cell15.BOX) {
            val affBox = getBoxPair(affRight)
            affectedBoxes.add(affBox)
            affectedBoxes.addAll(getAffectedBoxes(affBox, move))
        }
        if (warehouse[affLeft] == Cell15.BOX) {
            val affBox = getBoxPair(affLeft)
            affectedBoxes.add(affBox)
            affectedBoxes.addAll(getAffectedBoxes(affBox, move))
        }

        return affectedBoxes.toList()
    }

    private fun moveAllBoxes(boxes: List<DualDual<Int>>, move: Move15): Boolean {
//        println("about to move $boxes")
        val newY = if (move == Move15.UP) - 1 else + 1
        // box = ([yx][yx])
        val newBoxes = boxes.map { box ->
            (box.first.first + newY to box.first.second) to (box.second.first + newY to box.second.second)
        }
//        println("towards $newBoxes")
        val cells = newBoxes.flatMap { (left, right) -> listOf(warehouse[left], warehouse[right]) }
//        println("cells: $cells, ${cells.contains(Cell.WALL)}")
        if (cells.contains(Cell15.WALL)) return false // cant move, so do nothing
        else {
            // can move them
            // delete the current ones
            boxes.forEach { (left, right) ->
                warehouse[left] = Cell15.EMPTY
                warehouse[right] = Cell15.EMPTY
            }
            // redraw the moved ones
            newBoxes.forEach { (left, right) ->
                warehouse[left] = Cell15.BOX
                warehouse[right] = Cell15.BOX
            }
        }
        return true
    }

    private fun getBoxPair(location: Dual<Int>): DualDual<Int> {
        val row = warehouse[location.first]
        val boxes = row.drop(location.second + 1).takeWhile { it == Cell15.BOX }.count()  // no idea why +1
        val isRightBox = boxes % 2 == 0
        return if (isRightBox) (location.first to location.second-1) to location
        else location to (location.first to location.second+1)
    }

    override fun gps(): Int {
        return warehouse.flatMapIndexed { i, rows ->
            rows.mapIndexed { j, cell ->
                if (cell == Cell15.BOX) {
                    val (left, _) = getBoxPair(i to j)
                    100 * left.first + left.second
                } else 0
            }
        }.sum() / 2
    }
}

class D151: D15I {
    override val warehouse: MutableList<MutableList<Cell15>>
    override val moves: MutableList<Move15>
    override var robot = -1 to -1

    init {
        val (p1, p2) = File("../i24/15").readText().split("\n\n")
        warehouse = p1.split("\n").mapIndexed { i, str ->
            str.mapIndexed { j, ch ->
                when (ch) {
                    '#' -> Cell15.WALL
                    'O' -> Cell15.BOX
                    '.' -> Cell15.EMPTY
                    '@' -> { robot = i to j; Cell15.EMPTY }
                    else -> throw Exception("invalid object $ch")
                }
            }.toMutableList()
        }.toMutableList()
        moves = p2.replace("\n", "").map { ch ->
            when (ch) {
                '>' -> Move15.RIGHT
                '^' -> Move15.UP
                'v' -> Move15.DOWN
                '<' -> Move15.LEFT
                else -> throw Exception("invalid move >$ch<")
            }
        }.toMutableList()
    }

    override fun move() {
        val move = moves.removeFirst()
        val possibleMove = nextPos(robot, move)
        when (warehouse[possibleMove]) {
            Cell15.WALL -> return // do nothing
            Cell15.EMPTY -> robot = possibleMove  // move
            Cell15.BOX -> {  // the `move` made us run into the wall, check the next ones
                var after = possibleMove
                while (true) {
                    after = nextPos(after, move)  // check whats behind
                    when(warehouse[after]) {
                        Cell15.BOX -> continue
                        Cell15.WALL -> return // nothing we can do
                        Cell15.EMPTY -> {
                            warehouse[after] = Cell15.BOX  // put a box there (simulate move)
                            robot = possibleMove // move
                            warehouse[possibleMove] = Cell15.EMPTY
                            return
                        }
                    }
                }
            }
        }
    }

    override fun gps(): Int {
        return warehouse.flatMapIndexed { i, rows ->
            rows.mapIndexed { j, cell ->
                if (cell == Cell15.BOX) 100 * i + j else 0
            }
        }.sum()
    }
}


fun main() {
    val s2 = D152().run()
    val s1 = D151().run()
    println(s1 to s2)
}