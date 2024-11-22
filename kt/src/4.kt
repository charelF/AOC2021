enum class Move {
    UP, DOWN, RIGHT, LEFT
}

fun <T> ArrayDeque<T>.cycle(capacity: Int, next: T): ArrayDeque<T> {
    if (this.size >= capacity) {
        this.removeFirst()
        this.addLast(next)
    } else {
        this.addLast(next)
    }
    return this
}

data class Path(
    // first = y = outer array, second = x = inner array
    val pos: Pair<Int, Int>,
    val moves: ArrayDeque<Move> = ArrayDeque(3),
    val hist: Int = 3,

) {
    fun move(move: Move): Path {
        val newMoves = moves.cycle(hist, move)
        return when (move) {
            Move.UP -> Path(this.pos.first to this.pos.second-1, newMoves)
            Move.DOWN -> Path(this.pos.first to this.pos.second+1, newMoves)
            Move.RIGHT -> Path(this.pos.first+1 to this.pos.second, newMoves)
            Move.LEFT -> Path(this.pos.first-1 to this.pos.second, newMoves)
        }
    }

    fun possibleMovesByInertia(): Set<Move> {
        return when {
            moves.all { (it == Move.UP) || (it == Move.DOWN) } -> setOf(Move.RIGHT, Move.LEFT)
            moves.all { (it == Move.RIGHT) || (it == Move.LEFT) } -> setOf(Move.UP, Move.DOWN)
            moves.lastOrNull() == Move.UP -> setOf(Move.UP, Move.LEFT, Move.RIGHT)
            moves.lastOrNull() == Move.DOWN -> setOf(Move.DOWN, Move.LEFT, Move.RIGHT)
            moves.lastOrNull() == Move.RIGHT -> setOf(Move.UP, Move.DOWN, Move.RIGHT)
            moves.lastOrNull() == Move.LEFT -> setOf(Move.UP, Move.LEFT, Move.DOWN)
            else -> Move.entries.toSet()
        }
    }

    fun possibleMovesByLocation(): Set<Move> {
        return when {
            pos.first == 0
        }
    }

    fun possiblePaths(): List<Path> {

    }
}

fun main() {
    val input = """2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
""".split("\n").dropLast(1).map{it.map { it.digitToInt() }.toList()}

    println(input)
    val M = input.size
    val N = input.first().size

    fun nextPaths(path: Path): List<Path> {
        // constraint 1: borders
        // constraint 2: at mot 3 blocks in one area
        val possiblePaths: MutableList<Path> = mutableListOf()
        when {
            (
            ) ->
        }

        if  {
            // they move in the same y-direction 3 times in a row
            // only valid paths are increasing/decreasing .first
            if (path.current.first > 0) possiblePaths.add(path.goUp())
            if (path.current.first < M-1) possiblePaths.add(path.goDown())

        }
        return listOf()
    }


    // constrained optimisation: lets goo
//    fun f(directions: List<Dir>): Int {
//        var pos = 0 to 0
//        directions.forEach { dir ->
//            when (dir) {
//                Dir.UP ->
//            }
//        }
//    }






}