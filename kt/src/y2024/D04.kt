package y2024
import java.io.File


class D04 {
    fun main() {
        val a = File("i24/4").readLines().map { line ->
            line.toList()
        }

        var horicount = 0
        var vertcount = 0
        var diagcount = 0
        var mascount = 0
        repeat(a.size) { i ->
            repeat(a.size) { j ->
                println(i to j)
                try {
                    val hori ="${a[i][j]}${a[i][j+1]}${a[i][j+2]}${a[i][j+3]}"
                    if (hori == "XMAS" || hori == "SAMX") {
                        println("hori $hori")
                        horicount++
                    }
                } catch (e: Exception) {
//                    println(e)
                }
                try {
                    val vert = "${a[i][j]}${a[i+1][j]}${a[i+2][j]}${a[i+3][j]}"
                    if (vert == "XMAS" || vert == "SAMX") {
                        println("vert $vert")
                        vertcount++
                    }
                } catch (e: Exception) {
//                    println(e)
                }
                try {
                    val diag = "${a[i][j]}${a[i+1][j+1]}${a[i+2][j+2]}${a[i+3][j+3]}"
                    if (diag == "XMAS" || diag == "SAMX") {
                        println("diag $diag")
                        diagcount++
                    }
                } catch (e: Exception) {
//                    println(e)
                }
                try {
                    val diag = "${a[i][j]}${a[i+1][j-1]}${a[i+2][j-2]}${a[i+3][j-3]}"
                    if (diag == "XMAS" || diag == "SAMX") {
                        println("diag $diag")
                        diagcount++
                    }
                } catch (e: Exception) {
//                    println(e)
                }

                try {
                    val tor ="${a[i][j]}${a[i+1][j+1]}${a[i+2][j+2]}"
                    val tol ="${a[i][j+2]}${a[i+1][j+1]}${a[i+2][j]}"
                    if ((tor == "MAS" || tor == "SAM") && (tol == "MAS" || tol == "SAM")) {
                        println("mascount")
                        mascount++
                    }
                } catch (e: Exception) {
//                    println(e)
                }
            }
        }


        println(Triple(horicount,vertcount,diagcount))
        println(horicount + vertcount + diagcount)
        println(mascount)

    }
}