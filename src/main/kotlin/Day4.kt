import kotlin.math.max
import kotlin.math.min

class Day4 : AoC("day4") {
    override fun getFirstSolution(): String {
        val result = rawInputData.parseInput()
            .sumOf { (winningNbrs, myNbrs) ->
                val matches = winningNbrs.intersect(myNbrs).size

                return@sumOf if (matches > 0) 1 shl (matches - 1) else 0
            }

        return "Solution: $result"
    }

    private fun List<String>.parseInput(): List<Pair<Set<Int>, Set<Int>>> {
        val cardContents = this.map { it.substringAfter(':') }

        fun List<String>.convert(): List<Set<Int>> = this.map { spaceSeparatedNumbers ->
            spaceSeparatedNumbers.split(' ')
                .filter { it.isNotBlank() }
                .map { it.toInt() }
                .toSet()
        }

        val winningNumbers = cardContents.map { it.substringBefore('|') }.convert()
        val myNumbers = cardContents.map { it.substringAfter('|') }.convert()


        return winningNumbers.zip(myNumbers)
    }
}