package y2024

import java.io.File
import extensions.*
import kotlin.collections.contains
import kotlin.math.max
import kotlin.math.min

class D19 {
    val input = File("../i24/19").readText().split("\n\n")
    val patterns = input[0].split(", ").toSet()
    val designs = input[1].split("\n")
    val maxPatternLength = patterns.maxBy { it.count() }.count()

    /**
     * my original algorithm for part 1 splits each string everywhere and then
     * checks if this word exists. its too slow to do something similar for part 2
     */
    fun check(str: String): Boolean {
        for (i in 1 ..min(maxPatternLength, str.lastIndex)) {
            val (start, end) = str.splitAt(i)
            val eip = (end in patterns)
            val sip = (start in patterns)
            if (end.count() == 1) return eip
            if (sip && eip) return true  // ensures that we once we find 1 combination we return
            if (sip) {
                if (check(end)) return true
                // however we dont return false if check(end) is false since there are still others to try
            }
        }
        return false
    }

    /**
     * Find all the ways to generate a given pattern using two core concepts:
     * 1) **dynamic programming:** we split the problem into smaller problems and solve them recursively
     * 2) **memoization:** each time we compute the ways to generate a specific target, we store that
     * information and if we re-encounter it we can reuse that knowledge
     * Source: https://github.com/jonathanpaulson/AdventOfCode/blob/master/2024/19.py https://www.youtube.com/watch?v=SI5vjUmESrY
     */
    val memory = mutableMapOf<String, Long>()
    fun ways(target: String): Long {
        if (target in memory) return memory[target]!! // memoization
        var count = 0L
        if (target.isEmpty()) count = 1
        else {
            for (pattern in patterns) {
                if (target.startsWith(pattern)) {
                    count += ways(target.drop(pattern.length)) // dynamic programming
                }
            }
        }
        memory[target] = count
        return count
    }

    /**
     * lets go golfing
     */
    val m2 = mutableMapOf<String, Long>()
    fun w2(tg: String): Long {
        return m2.getOrPut(tg) {
            if (tg.isEmpty()) 1 else patterns
                .filter { tg.startsWith(it) }
                .sumOf { w2(tg.drop(it.length)) }
        }
    }

    fun main() {
        designs.sumOf {check(it).toInt() }.print()
        designs.sumOf { (ways(it) > 0).toInt() }.print()
        designs.sumOf { ways(it) }.print()
        designs.sumOf { (w2(it) > 0).toInt() }.print()
        designs.sumOf { w2(it) }.print()
    }
}

fun main() {
    D19().main()
}