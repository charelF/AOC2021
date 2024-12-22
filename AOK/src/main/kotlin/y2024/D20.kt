package y2024

import extensions.*
import java.io.File
import java.util.PriorityQueue


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
        data class STARTED(val start: Dual<Int>): CheatState()
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
            }
            val neighbours = edge.state.pos.getNeighbours(DistanceMetric.MANHATTAN)
                .filter { it.isWithin(bounds) }
                .mapNotNull { coords ->
                    val inWall = (racetrack[coords] == '#')
                    when (edge.state.cheatState) {
                        is CheatState.BEFORE -> {
                            if (inWall) Edge2(
                                // stupid puzzle only through reddit i find out the cheat has to start outside the wall
                                State2(coords, CheatState.STARTED(edge.state.pos)),
                                edge.score + 1,
                                edge.cheatDuration + 1
                            )
                            else Edge2(State2(coords, CheatState.BEFORE), edge.score + 1, edge.cheatDuration)
                        }

                        is CheatState.STARTED -> {
                            // from studying the examples it seems that once the cheat starts it doesnt really need to only
                            // go through walls but can also do normal paths so in other words it just expires the 20
                            // steps and then its done?

                            if (edge.cheatDuration >= cheatMaxDuration) {
                                if (inWall) null // not allowed in wall anymore
                                else Edge2(
                                    State2(coords, CheatState.ENDED(edge.state.cheatState.start, coords)),
                                    edge.score + 1,
                                    edge.cheatDuration
                                )
                            } else {
                                if (inWall) Edge2(
                                        State2(coords, edge.state.cheatState),
                                        edge.score + 1,
                                        edge.cheatDuration + 1
                                    )
                                else Edge2(
                                    State2(coords, CheatState.ENDED(edge.state.cheatState.start, coords)),
                                    edge.score + 1,
                                    edge.cheatDuration
                                )
                            }
                        }

                        is CheatState.ENDED -> {
                            if (inWall) null // no no
                            else Edge2(State2(coords, edge.state.cheatState), edge.score + 1, edge.cheatDuration)
                        }
                    }
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
        val shortest = solve2(20, 55).print()
    }
}

fun main() {
    D20().p2()
//    D20().p2()
}