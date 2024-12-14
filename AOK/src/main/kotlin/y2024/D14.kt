package y2024

import extensions.*
import java.io.File

class D14 {
    val bounds = 103 to 101
    val robots = File("../i24/14").readLines().map { line ->
        val (px, py, vx, vy) = Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)")
            .findAll(line).toList().first().groupValues.drop(1).map { it.toInt() }
        Robot(py to px, vy to vx)
    }

    data class Robot(
        val pos: Dual<Int>,
        val velocity: Dual<Int>
    )

    fun Robot.move(steps: Int): Robot {
        var newpos = this.pos
        repeat(steps) { newpos = (newpos + velocity + bounds) mod bounds }
        return Robot(newpos, velocity)
    }

    fun Robot.quadrant(): Int? {
        return when {
            (pos.first < bounds.first/2) && (pos.second < bounds.second/2) -> 1
            (pos.first > bounds.first/2) && (pos.second < bounds.second/2) -> 2
            (pos.first < bounds.first/2) && (pos.second > bounds.second/2) -> 3
            (pos.first > bounds.first/2) && (pos.second > bounds.second/2) -> 4
            else -> null
        }
    }

    fun printLevel(level: Array<BooleanArray>, printer: (String) -> Unit) {
        level.forEach { row ->
            row.forEach { cell ->
                if (cell) print("░░") else print("  ")
            }.also { print("\n") }
        }
    }

    fun draw(robots: List<Robot>, i: Int, save: Boolean) {
        val map = Array(bounds.first) { BooleanArray(bounds.second) { false } }
        robots.forEach { r -> map[r.pos.first][r.pos.second] = true }
        val file = File("ressources/y2024/d14/$i.txt")
        file.parentFile.mkdirs()
        if (!save) printLevel(map) { print(it) }
        else file.printWriter().use { printLevel(map) { print(it) } }
    }

    fun main() {
        robots.mapNotNull { robot -> robot.move(100).quadrant() }
            .groupBy{ it }
            .mapValues { it.value.size }
            .values
            .reduce(Int::times)
            .also(::println)

        for (i in 0 until 10000) {
            if (i != 7383) continue
            print(i)
            // to find the solution, i generated 10k files and checked manually
            draw(robots.map {it.move(i)}, i, save=false)
        }
    }
}


fun main() = D14().main()