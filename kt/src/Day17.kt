import java.io.File
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

enum class Move {
    UPPP, DOWN, RGHT, LEFT
}

fun <T> Triple<T?, T?, T?>.cycle(next: T): Triple<T?, T?, T> {
    // any FIFO structure would be good. ArrayDeque structure seems a bit weird in kotlin,
    // also requires careful considerations of copy-by-ref problems so since the history is 3
    // anyway a triple might do the job just fine enough for now.
    // will be used in the order: remove from .first <-- [. . . ] <- new elements arriving at .third
    return Triple(this.second, this.third, next)
}

class Puzzle(
    val mapp: List<List<Int>>,
    val mapDimensions: Pair<Int, Int> = mapp.size to mapp.first().size,
) {
    private fun possibleMovesByInertia(path: Path): Set<Move> {
        // actually kotlin pattern matching is a bit weak ...
        return when {
            path.moves == Triple(Move.UPPP, Move.UPPP, Move.UPPP) -> setOf(Move.RGHT, Move.LEFT)
            path.moves == Triple(Move.DOWN, Move.DOWN, Move.DOWN) -> setOf(Move.RGHT, Move.LEFT)
            path.moves == Triple(Move.RGHT, Move.RGHT, Move.RGHT) -> setOf(Move.UPPP, Move.DOWN)
            path.moves == Triple(Move.LEFT, Move.LEFT, Move.LEFT) -> setOf(Move.UPPP, Move.DOWN)
            path.moves.third == Move.UPPP -> setOf(Move.LEFT, Move.UPPP, Move.RGHT)
            path.moves.third == Move.DOWN -> setOf(Move.LEFT, Move.DOWN, Move.RGHT)
            path.moves.third == Move.RGHT -> setOf(Move.UPPP, Move.RGHT, Move.DOWN)
            path.moves.third == Move.LEFT -> setOf(Move.UPPP, Move.LEFT, Move.DOWN)
            else -> Move.entries.toSet()
        }
    }

    private fun possibleMovesByLocation(path: Path): Set<Move> {
        val moves: MutableSet<Move> = mutableSetOf()
        if (path.pos.first > 0) moves.add(Move.UPPP)
        if (path.pos.first < mapDimensions.first - 1) moves.add(Move.DOWN)
        if (path.pos.second > 0) moves.add(Move.LEFT)
        if (path.pos.second < mapDimensions.second - 1) moves.add(Move.RGHT)
        return moves
    }

    private fun nextPaths(path: Path): List<Pair<Path, Int>> {
        val possibleMoves = possibleMovesByInertia(path) intersect possibleMovesByLocation(path)
        return possibleMoves
            .map { move -> path.next(move) }
            .map { move -> move to mapp[move.pos.first][move.pos.second] }
    }

    fun nextNodes(node: Node): List<Node> = nextPaths(node.path).map { (p,l) -> Node(p, l+node.loss)  }
}

data class Node(
    val path: Path,
    val loss: Int,
): Comparable<Node> {
    override fun compareTo(other: Node): Int {
        return this.loss.compareTo(other.loss)
    }
}


data class Path(
    // first = y = outer array, second = x = inner array
    val pos: Pair<Int, Int> = 0 to 0,
    val moves: Triple<Move?, Move?, Move?> = Triple(null, null, null),
) {
    fun next(move: Move): Path {
        val newMoves = moves.cycle(move)
        return when (move) {
            Move.UPPP -> Path(this.pos.first-1 to this.pos.second, newMoves)
            Move.DOWN -> Path(this.pos.first+1 to this.pos.second, newMoves)
            Move.RGHT -> Path(this.pos.first to this.pos.second+1, newMoves)
            Move.LEFT -> Path(this.pos.first to this.pos.second-1, newMoves)
        }
    }
}

fun solveDijkstra(input: List<List<Int>>): Node? {
    // initialise the puzzle to set up the Map
    val puzzle = Puzzle(input)

    // Dijkstra algorithm to find the shortest path between two nodes in a graph
    val queue = PriorityQueue<Node>()

    // add starting node to queue
    queue.add(Node(Path(), 0,))

    // define end goal to reach
    val endPos = puzzle.mapDimensions.first - 1 to puzzle.mapDimensions.second - 1

    // map to track min loss
    val visitedPaths: LinkedHashMap<Path, Int> = linkedMapOf(Path() to 0)

    while (queue.isNotEmpty()) {
        // remove smallest
        val node = queue.remove() // get node with lowest loss

        // return if we are done
        if (node.path.pos == endPos) return node

        // else get next nodes
        puzzle.nextNodes(node).forEach { nextNode ->
            // if this new path is better than an existing path, we replace it
            if (nextNode.loss < (visitedPaths[nextNode.path] ?: Int.MAX_VALUE)) {
                visitedPaths[nextNode.path] = nextNode.loss
                queue.add(nextNode)
            }
        }
    }
    return null
}

fun d17() {
    val filename = "inputs23/17"
    val input = File(filename)
        .readText()
        .split("\n")
        .dropLast(1)
        .map { string ->
            string.map { char ->
                char.digitToInt()
            }.toList()
        }

    measureTimeMillis {
        val solution = solveDijkstra(input)
        println(solution)
    }.let {println("found in $it ms")}
}