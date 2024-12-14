package y2024

import extensions.*
import java.io.File

class D14 {
//    val bounds = 7 to 11
    val bounds = 103 to 101

    data class Robot(
        val pos: Dual<Int>,
        val velocity: Dual<Int>
    )

    fun Robot.move(steps: Int): Robot {
        var newpos = this.pos
        repeat(steps) {
            newpos = (newpos + velocity + bounds) mod bounds
        }
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



    val robots = File("../i24/14").readLines().map { line ->
        val (px, py, vx, vy) = Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)")
            .findAll(line).toList().first().groupValues.drop(1).map { it.toInt() }
        Robot(py to px, vy to vx)
    }

    fun main() {
        robots.mapNotNull { robot ->
            robot.move(100).quadrant()
        }.groupBy{ it }.mapValues { it.value.size }.values.reduce(Int::times)
            .also(::println)
    }

//    241798752  too high
//      241993440
//    229069152

}


fun main() = D14().main()