import extensions.toInt
import y2024.d02
import java.io.File

fun main() {
    val mul = """mul\((\d+),(\d+)\)""".toRegex()
    val disabled = """don\'t\(\).*?(?:do\(\)|${'$'})""".toRegex()
    val text = File("i24/3")
        .readText().replace("\n", "-") // important for regex!
    fun find(input: String): Int {
        return mul.findAll(input).sumOf { match ->
            val (x, y) = match.destructured
            x.toInt() * y.toInt()
        }
    }
    println(find(text))
    println(find(text.replace(disabled, "")))
}