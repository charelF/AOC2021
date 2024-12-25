package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue
import kotlin.math.abs


class D20 {
    val racetrack = File("../i24/20s").readLines().map { it.toList() }
    val bounds = racetrack.size to racetrack.first().size
    val start = racetrack.dualIndexOf { it == 'S' }.print()
    val end = racetrack.dualIndexOf { it == 'E' }.print()
    val startState = State(start, 1)
    val walls = racetrack.flatMapIndexed { i, row ->
        row.mapIndexedNotNull { j, ch ->
            if (ch=='#') i to j else null
        }
    }

    data class State (
        val pos: Dual<Int>,
        val cheatsRemaining: Int,
    )

    data class Edge(
        val state: State,
        val score: Int
    ): Comparable<Edge> {
        override fun compareTo(other: Edge) = score.compareTo(other.score)
    }

    fun solve(track: List<List<Char>>, limit: Int): Pair<Boolean, Edge> {
        var edge = Edge(startState, 0)
        val queue = PriorityQueue<Edge>().also{it.add(edge)}
        val visited: MutableMap<State, Int> = mutableMapOf(edge.state to edge.score)

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == end) return true to edge
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .map { Edge(State(it, edge.state.cheatsRemaining), edge.score+1) }
                .filter { track[edge.state.pos] != '#'}
                .filter { it.score <= limit } // needs to be better than cheat otherwise lame
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate {it.state to it.score})
        }
        return false to edge
    }

    fun p1() {
        val shortest = solve(racetrack, Int.MAX_VALUE)
        println(shortest)
        println("total walls: ${walls.size}")
        val improvement = 40
        walls.mapIndexed { i, wall ->
            println(i)
            val track = racetrack.map { it.toMutableList() }.toMutableList()
            track[wall] = '.'
            solve(track, shortest.second.score - improvement).first.toInt()
        }.sum().print()
    }

    /**
     * so clean omg
     */
    sealed class CheatState() {
        data object BEFORE: CheatState()
        data class ACTIVE(val start: Dual<Int>, val possibleEnd: Dual<Int>?): CheatState()
        data class ENDED(val start: Dual<Int>, val end: Dual<Int>): CheatState()
    }

    data class State2 (
        val pos: Dual<Int>,
        val cheatState: CheatState,
    )

    data class Edge2 (
        val state: State2,
        val score: Int,
        val cheatDuration: Int // duration isn't part of the state/cheatstate because a cheat is only uniquely identified by its start and end position
    ): Comparable<Edge2> {
        override fun compareTo(other: Edge2) = score.compareTo(other.score)
    }


    /**
     * returns all positions where the cheat could land us
     */
    fun cheat(at: Dual<Int>, duration: Int): List<Pair<Dual<Int>, Int>> {
        val reached = at.getNeighbours(DistanceMetric.MANHATTAN, duration)
        val distances = reached.map { c -> c to abs(c.first - at.first) + abs(c.second - at.second) }
        return distances
    }

    fun solve2(cheatMaxDuration: Int, scoreToBeat: Int): Map<Int,Int> {
        var edge = Edge2(State2(start, CheatState.BEFORE), 0, 0)
        val queue = PriorityQueue<Edge2>().also { it.add(edge) }
        val visited: MutableMap<State2, Int> = mutableMapOf(edge.state to edge.score)
        val cheats: MutableMap<Int, Int> = mutableMapOf()

        while (queue.isNotEmpty()) {
            edge = queue.poll()
            if (edge.state.pos == end) {
                println(edge)
                cheats[edge.score] = cheats.computeIfAbsent(edge.score) { 0 } + 1

                val i = (edge.state.cheatState as CheatState.ACTIVE).start
                val j = (edge.state.cheatState as CheatState.ACTIVE).possibleEnd
                racetrack.forEachIndexed { ii, line ->
                    line.forEachIndexed { jj, char ->
                        if (i == ii to jj) print("1")
                        else if (j == ii to jj) print("2")
                        else print(char)
                    }
                    println()
                }

            }
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .mapNotNull { coords ->



                    val thisInWall = (racetrack[coords] == '#')
                    val prevInWall = (racetrack[edge.state.pos] == '#')
                    val cheatingMustStop = (edge.cheatDuration >= cheatMaxDuration)


                    // completely new approach:
                    // just do regular shortest path
                    // if not yet cheated, at one moment, just get all neighbours within Manhattan 20
                    // they must land on . fields. this is the cheeat, then its over



                    val x = when (edge.state.cheatState) {

                        is CheatState.BEFORE -> {
                            if (thisInWall) Edge2(
                                // stupid puzzle only through reddit i find out the cheat has to start outside the wall
                                State2(coords, CheatState.ACTIVE(edge.state.pos, null)),
                                edge.score + 1,
                                edge.cheatDuration + 1
                            )
                            else Edge2(State2(coords, CheatState.BEFORE), edge.score + 1, edge.cheatDuration)
                        }

                        is CheatState.ACTIVE -> {
                            // from studying the examples it seems that once the cheat starts it doesnt really need to only
                            // go through walls but can also do normal paths so in other words it just expires the 20
                            // steps and then its done?
                            // i think once we start, the cheat goes until it expires

                            // if the cheat is active, we can go through walls or not
                            // everytime we enter a valid path, we have to record the spot where we left the wall and mark it as possible cheat end

//                            if (thisInWall && prevInWall)

                            if (cheatingMustStop && thisInWall) {
                                null
                            } else if (cheatingMustStop && !thisInWall) {
                                Edge2(
                                    State2(coords, CheatState.ENDED(edge.state.cheatState.start, edge.state.cheatState.possibleEnd ?: coords)),
                                    edge.score + 1,
                                    edge.cheatDuration + 1
                                )
                            } else if (thisInWall) {
                                Edge2(
                                    State2(coords, edge.state.cheatState),
                                    edge.score + 1,
                                    edge.cheatDuration + 1
                                )
                            } else if (prevInWall && !thisInWall) {
                                // the moment we leave
                                if (coords == 8 to 3) {
                                    println("-------")
                                    println(Edge2(
                                    State2(coords, CheatState.ACTIVE(edge.state.cheatState.start, coords)),
                                    edge.score + 1,
                                    edge.cheatDuration + 1
                                ))
                                    println(thisInWall)
                                    println(coords)
                                    println("\n")
                                }
                                Edge2(
                                    State2(coords, CheatState.ACTIVE(edge.state.cheatState.start, coords)),
                                    edge.score + 1,
                                    edge.cheatDuration + 1
                                )
                            } else if (!prevInWall && !thisInWall) {
                                Edge2(
                                    State2(coords, edge.state.cheatState),
                                    edge.score + 1,
                                    edge.cheatDuration + 1
                                )
                            } else {
                                TODO() // shouldnt happen
                            }
                        }

                        is CheatState.ENDED -> {
                            if (thisInWall) null // no no
                            else Edge2(State2(coords, edge.state.cheatState), edge.score + 1, edge.cheatDuration)
                        }



                    }

                    if (coords == (7 to 5) && (edge.state.cheatState is CheatState.ACTIVE && edge.state.cheatState.possibleEnd== 8 to 3)) {
                        println("%%%%%%%%%%")
                        println(edge)
                        println(thisInWall)
                        println(prevInWall)
                        println(x)
                    }

                    x
                }
                .filter { it.score < scoreToBeat }
                .filter { it.score < (visited[it.state] ?: Int.MAX_VALUE) }

            queue.addAll(neighbours)
            visited.putAll(neighbours.associate { it.state to it.score })
        }
        return cheats//.mapKeys { scoreToBeat - it.key }
    }

    //Cheats don't need to use all 20 picoseconds; cheats can last any amount of time up to and including 20 picoseconds (but can still only end when the program is on normal track). Any cheat time not used is lost; it can't be saved for another cheat later.
    // find all the cheats -> a cheat is identified by the start and end position

    fun p2() {
        // fastest without cheating = 84
//        val shortest = solve2(20, 12).print()

        cheat(3 to 1, 2).print()
    }

}

fun main() {
    D20().p2()
//    D20().p2()
}