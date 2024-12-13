package y2024

import java.io.File

class D05 {
    fun main() {
        val (ruleSection, numSection) = File("../i24/5").readText().split("\n\n")
        val rules = ruleSection.split("\n")
        val numberSequences = numSection
            .split("\n")
            .map { line -> line.split(",").map { it.toInt() } }

        val rulePairs = rules
            .map { rule -> rule.split("|").map { it.toInt() } }
            .map { (first, second) -> first to second }
            .toSet()

        // Comparator: Compares its two arguments for order.
        // Returns zero if the arguments are equal, a negative number if the first argument is less than the second,
        // or a positive number if the first argument is greater than the second.
        val comparator = Comparator<Int> { first, second ->
            // 47|53, means that if an update includes both page number 47 and page number 53,
            // then page number 47 must be printed at some point before page number 53
            when {
                rulePairs.contains(first to second) -> -1 // return -1 because first (e.g. 47) needs to be in front of 53 -> so the first is smaller
                rulePairs.contains(second to first) -> 1  // return 1 because second is smaller, so first is bigger
                else -> 0 // they are equal -> there are no direct dependencies between them, so it doesnt matter where they are
                // e.g. if a and b are equal, then d a b e == d b a e. This however is never the case in this advent
                // since if it would be, there would be more than 1 solution, which could not be verified by the AOC verifier
            }
        }

        val a1 = numberSequences.sumOf { seq ->
            // pair-wise comparison is enough to know they are in order. for (a,b,c), if a and b are in order and
            // b and c are in order, then a and c are also in order
            val result = seq.zipWithNext(comparator::compare)
            if (result.all { it == -1 }) seq[seq.size / 2] else 0  // for the correct ones, return the middle
        }

        val a2 = numberSequences.sumOf { seq ->
            // when sorting, we can use the same comparator and let the sort function deal with the actual sorting,
            // we just give it the order
            val sorted = seq.sortedWith(comparator)
            if (sorted != seq) sorted[sorted.size / 2] else 0 // we just return the ones that were sorted
        }
        println(a1 to a2) // (5713, 5180)
    }
}

fun main() { D05().main() }