package y2024

import java.io.File

class D03 {
    private val mulPattern = """mul\((\d+),(\d+)\)""".toRegex()
    private val dontdoPattern = """don't\(\).*?(?:do\(\)|$)""".toRegex()  // .*? = as little as possible

    fun main() {
        val text = File("../i24/3")
            .readText().replace("\n", "-") // important for regex!

        fun find(input: String): Int {
            return mulPattern.findAll(input).sumOf { match ->
                val (x, y) = match.destructured
                x.toInt() * y.toInt()
            }
        }
        println(find(text) to find(text.replace(dontdoPattern, "")))
    }
}

fun main() { D03().main() }