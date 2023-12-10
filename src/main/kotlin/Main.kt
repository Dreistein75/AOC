import java.io.File

fun main() {
    val day: AoC = Day10()

    println("Exercise 1: ${day.getFirstSolution()}")
    println("Exercise 2: ${day.getSecondSolution()}")
}

abstract class AoC(
    filename: String
) {
    protected val rawInputData: List<String> = File("src/main/resources/$filename.txt").readLines()
    open fun getFirstSolution(): String = "To be done"
    open fun getSecondSolution(): String = "To be done"
}