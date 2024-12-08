package y2024

import java.io.File
import extensions.*

class D08 {

    fun getP1AntiNodes(pair: Pair<Pair<Int, Int>, Pair<Int, Int>>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val diff = pair.second - pair.first // that means, first + diff = second
        return pair.first - diff to pair.second + diff
    }

    fun getP2AntiNodes(
        pair: Pair<Pair<Int, Int>, Pair<Int, Int>>,
        bounds: Pair<Int, Int>
    ): Sequence<Pair<Pair<Int, Int>, Pair<Int, Int>>> = sequence {
        val diff = pair.second - pair.first
        var (first, second) = pair
        while (first.isWithIn(bounds) || second.isWithIn(bounds)) {
            yield(first to second)
            first -= diff
            second += diff
        }
    }

    fun main() {
        val lines = File("i24/8").readLines()
        val m = lines.size
        val n = lines[0].count()

        val antennas: MutableMap<Char, MutableList<Pair<Int, Int>>> = mutableMapOf()
        lines.mapIndexed { i, line ->
            line.mapIndexed { j, c ->
                if (c != '.') {
                    antennas.computeIfAbsent(c) { mutableListOf() }.add(i to j)
                }
            }
        }

        val p1 = antennas.flatMap { (_, coordinates) ->
            coordinates
                .pairwise(withSelf = false)
                .map(::getP1AntiNodes)
                .map { pair -> pair.toList() }.flatten()
                .filter { pair -> pair.isWithIn(m to n) }
        }.toSet()

        val p2 = antennas.flatMap { (_, coordinates) ->
            coordinates
                .pairwise(withSelf = false)
                .map { pair -> getP2AntiNodes(pair, m to n) }
                .map { seq -> seq.flatMap { pair -> pair.toList() } }.flatten()
                .filter { pair -> pair.isWithIn(m to n) }
        }.toSet()

        println(p1.size to p2.size)
    }
}