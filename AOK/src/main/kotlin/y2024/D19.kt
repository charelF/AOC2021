package y2024

import java.io.File
import extensions.*
import kotlin.math.min

class D19 {
    val input = File("../i24/19").readText().split("\n\n")
    val patterns = input[0].split(", ").toSet().print()
    val designs = input[1].split("\n").print()
    val maxPatternLength = patterns.maxBy { it.count() }.count()

    fun main() {
        designs.sumOf {d ->
            check(d).toInt()
        }.print()

//        designs.map {d ->
//            check2(d)
//        }.print()

//        check(designs[1])
    }

    fun String.splitAt(i: Int): Pair<String, String> {
        return this.take(i) to this.drop(i)
    }

    fun check(str: String): Boolean {
        for (i in 1 ..min(maxPatternLength, str.lastIndex)) {
            val (start, end) = str.splitAt(i)
            val eip = (end in patterns)
            val sip = (start in patterns)
//            println("start [$start] end [$end]")
            if (end.count() == 1) return eip
            if (sip && eip) return true  // ensures that we once we find 1 combination we return
            if (sip) {
                if (check(end)) {
                    return true
                }
                // however we dont return false if check end is false since there still
            }
        }
        return false
    }

    fun check2(str: String): Boolean {
        for (i in 1 ..min(maxPatternLength, str.lastIndex)) {
            val (start, end) = str.splitAt(i)
            println("start [$start] end [$end]")
            val eip = (end in patterns)
            val sip = (start in patterns)
            if (end.count() <= 1) return eip
            if (sip && eip) return true
            if (sip) {
                if (check2(end)) {
                    return true
                }
            }
        }
        return false
    }

//    fun check(str: String): Boolean {
//        for (i in 1 ..min(maxPatternLength, str.count())) {
//            val start = str.substring(0, i)
//            val end = str.substring(i)
//            val eip = (end in patterns)
//            val sip = (start in patterns)
//            if (end.count() == 1) return eip
//            if (sip && eip) return true
//            if (sip) {
//                if (check(end)) {
//                    return true
//                }
//            }
//        }
//        return false
//    }

//    fun check2(str: String) {
//        val combinations = setOf<List<String>>()
//
//        fun inner(str: String, hist: List<String>): Pair<Boolean,List<String>> {
//            for (i in 1 ..min(maxPatternLength, str.count())) {
//                val start = str.substring(0, i)
//                val end = str.substring(i)
//                val eip = (end in patterns)
//                val sip = (start in patterns)
//                if ((end.count() == 1) && eip) {
//                    return true to hist + start + end
//                }
//                if (sip) {
//                    val res = inner(end, hist + start)
//                    if (res.first) {
//                        return res
//                    }
//                }
//            }
//            return false to listOf()
//        }
//    }
}

fun main() {
    D19().main()
}