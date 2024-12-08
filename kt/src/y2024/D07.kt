package y2024

import extensions.toInt
import java.io.File
import kotlin.math.pow


class D07 {
    enum class Operator {
        PLUS, MULT
    }

    fun p1() {
        val inp = File("i24/7").readLines().map { line ->
            val list = line.split(": ")
            val first = list[0].toLong()
            val nums = list[1].split(" ").map { it.toLong() }
            val ncom = 2.0
                .pow(nums.size - 1)
                .toInt()
            var isPossible = false
            for (i in 0..<ncom) {
                val comb = i.toUInt().toString(radix = 2)
                    .padStart(nums.size - 1)
                    .map { it == '1' }
                val res = nums.reduceIndexed { i, acc, num ->
                    if (comb[i - 1]) acc * num else acc + num
                }
                if (res == first) {
                    isPossible = true
                    break
                }
            }
            if (isPossible) first else 0
        }.sum()
        println(inp)
    }


//    if (n == 0) System.out.println(prefix);
//    else {
//        for (int i = 0; i < n; i++)
//        permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n));
//    }
//
////    fun <E> Collection<E>.permutations(repetition: Boolean): Iterable<Collection<E>> {
//
//    fun f(lst: Collection<E>): Collection<E> {
//        if (lst.isEmpty()) return lst
//        else {
//            for (i in lst.indices) {
//                f()
//            }
//        }
//        val perms = f(lst.drop(1))
//        for (perm in perms) {
//            for (i in )
//        }
//
//    }
//        if (this.isEmpty()) {
//
//        }
//    }

    fun p2() {
        val inp = File("i24/7").readLines().map { line ->
            val list = line.split(": ")
            val first = list[0].toLong()
            val nums = list[1].split(" ").map { it.toLong() }
            val ncom = 2.0
                .pow(nums.size - 1)
                .toInt()
            var isPossible = false
            for (i in 0..<ncom) {
                val comb = i.toUInt().toString(radix = 2)
                    .padStart(nums.size - 1)
                    .map { it == '1' }
                val res = nums.reduceIndexed { i, acc, num ->
                    if (comb[i - 1]) acc * num else acc + num
                }
                if (res == first) {
                    isPossible = true
                    break
                }
            }
            if (isPossible) first else 0
        }.sum()
        println(inp)
    }


    fun <E> perm(lst: List<E>): MutableList<List<E>> {
        val choices = lst
        val accu: MutableList<List<E>> = mutableListOf()
        fun x(amount: Int, sofar: List<E>): List<E> {
            if (amount == 0) {
                return sofar
            }
            choices.forEach { choice ->
                accu.add(x(amount-1, sofar + choice))
                return sofar + choice
            }
            return sofar
        }
        return accu
    }

    fun main() {
        println(perm(listOf(1,2,3)))
    }
}



//fun <T> generateCombinations(
//    elements: List<T>,
//    length: Int,
//    withRepetition: Boolean
//): Sequence<List<T>> = sequence {
//    if (length == 0) {
//        yield(emptyList())
//        return@sequence
//    }
//
//    val indices = IntArray(length) { 0 }
//    val n = elements.size
//
//    while (true) {
//        // Yield the current combination
//        yield(indices.map { elements[it] })
//
//        // Increment indices
//        var carry = true
//        for (i in length - 1 downTo 0) {
//            if (withRepetition || indices[i] < n - 1) {
//                indices[i]++
//                if (!withRepetition) {
//                    for (j in i + 1 until length) {
//                        indices[j] = indices[i]
//                    }
//                }
//                carry = false
//                break
//            } else {
//                indices[i] = 0
//            }
//        }
//        if (carry) break
//    }
//}
//
//
//class D09 {
//
//    fun main() {
//        val elements = listOf('a', 'b', 'c')
//        val length = 3
//
//        println("With repetition:")
//        generateCombinations(elements, length, withRepetition = true).forEach {
//            println(it)
//        }
//
//        println("\nWithout repetition:")
//        generateCombinations(elements, length, withRepetition = false).forEach {
//            println(it)
//        }
//    }
//}