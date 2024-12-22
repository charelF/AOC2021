package y2024

import java.io.File

class D22 {
    val seeds = File("../i24/22").readLines().map(String::toLong)

    fun secretNumber(seed: Long, iterations: Int): List<Long> {
        var num = seed
        return (0 until iterations).map {
            num = (num xor (num * 64L)) % 16777216L
            num = (num xor (num / 32L)) % 16777216L
            num = (num xor (num * 2048)) % 16777216L
            num
        }
    }

    fun priceChangeMap(seed: Long, iterations: Int): Map<List<Long>, Long> {
        return (listOf(seed) + secretNumber(seed, iterations))  // need to include seed to compute 1st diff
            .map { it % 10 } // get only the last digit
            .windowed(2).map { (l1,l2) -> l2 to l2 - l1 } // for each digit, get the difference to previous
            .windowed(4).map { fourPairs ->  // this is like [(0€,-3), (6€, 6), (5€, -1), (4€, -1)
                fourPairs.map { it.second } to fourPairs.last().first /// now its ([-3, 6, -1, -1], 4€)
            } // for each four differences, find the price it would result in
            .groupBy { it.first } // put into a map where the key is a list of four differences
            .mapValues { (k,v) -> v.first().second } // if a four-difference occurs more than once, only keep the first (`first`) price (`second`)
    }

    fun main() {
        seeds.map { secretNumber(it, 2000).last() }.sum().print()

        seeds
            .map { priceChangeMap(it, 2000) } // computes all possible prices
            .flatMap { it.entries } // now we do the classic: combining list of maps into a single map and sum the values
            .groupingBy { it.key }
            .fold(0L) { sum, entry -> sum + entry.value }
            .maxBy { it.value } // finally find the max
            .print()
    }
}

fun main() = D22().main()