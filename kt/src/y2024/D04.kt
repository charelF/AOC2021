package y2024
import java.io.File


class D04 {
    fun main() {
        val a = File("i24/4").readLines().map { line ->
            line.toList()
        }
        var q1c = 0
        var q2c = 0
        repeat(a.size) { i ->
            repeat(a.size) { j ->
                try {
                    val hori ="${a[i][j]}${a[i][j+1]}${a[i][j+2]}${a[i][j+3]}"
                    if (hori == "XMAS" || hori == "SAMX") q1c++
                } catch(_: Exception) { }
                try {
                    val vert = "${a[i][j]}${a[i+1][j]}${a[i+2][j]}${a[i+3][j]}"
                    if (vert == "XMAS" || vert == "SAMX") q1c++
                } catch(_: Exception) { }
                try {
                    val diag = "${a[i][j]}${a[i+1][j+1]}${a[i+2][j+2]}${a[i+3][j+3]}"
                    if (diag == "XMAS" || diag == "SAMX") q1c++
                } catch(_: Exception) { }
                try {
                    val diag = "${a[i][j]}${a[i+1][j-1]}${a[i+2][j-2]}${a[i+3][j-3]}"
                    if (diag == "XMAS" || diag == "SAMX") q1c++
                } catch(_: Exception) { }
                try {
                    val tor ="${a[i][j]}${a[i+1][j+1]}${a[i+2][j+2]}"
                    val tol ="${a[i][j+2]}${a[i+1][j+1]}${a[i+2][j]}"
                    if ((tor == "MAS" || tor == "SAM") && (tol == "MAS" || tol == "SAM")) q2c++
                } catch(_: Exception) { }
            }
        }
        println(q1c to q2c)
    }
}