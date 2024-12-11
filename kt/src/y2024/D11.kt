package y2024

import java.io.File
import extensions.*

class D11 {
    fun blink(num: Long): List<Long> {
        return when {
            num == 0L -> listOf(1L)
            num.toString().count() % 2 == 0 -> {
                val s = num.toString()
                val x = s.chunked( s.count()/2).map { it.toLong() }
                x
            }
            else -> listOf(num*2024)
        }
    }

    fun main() {
        var a = File("i24/11").readText().split(" ").map {it.toLong()}
        repeat(25) {
            a = a.flatMap { blink(it) }
        }
        println(a.size)

    }
}