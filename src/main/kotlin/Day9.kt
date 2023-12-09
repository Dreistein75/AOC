class Day9 : AoC("day9") {
    override fun getFirstSolution(): String {
        return rawInputData.solveToday(
            keepRelevantNumberOfReducedLine = { this.last() },
            extrapolationMethodForNumberAbove = { a, b -> a + b },
        )
    }

    override fun getSecondSolution(): String {
        return rawInputData.solveToday(
            keepRelevantNumberOfReducedLine = { this.first() },
            extrapolationMethodForNumberAbove = { a, b -> a - b },
        )
    }

    private fun List<String>.parseInput(): List<List<Int>> = this.map { line ->
        line.split(' ')
            .filter { it.isNotBlank() }
            .map { it.toInt() }
    }

    private fun List<Int>.generateReducedLists(): List<List<Int>> = (1 until this.size)
        .runningFold(this) { currentList, _ -> currentList.zipWithNext { a, b -> b - a} }

    private fun List<String>.solveToday(
        keepRelevantNumberOfReducedLine: List<Int>.() -> Int,
        extrapolationMethodForNumberAbove: (Int, Int) -> Int
    ): String {
        val result = this.parseInput()
            .sumOf { inputLine ->
                val relevantNumberOfReducedLines = inputLine.generateReducedLists()
                    .map { it.keepRelevantNumberOfReducedLine() }

                return@sumOf relevantNumberOfReducedLines.indices
                    .reversed()     // not needed, still instructive
                    .fold(0) { acc, lineIndex ->
                        extrapolationMethodForNumberAbove(relevantNumberOfReducedLines[lineIndex], acc)
                    }
                    .toInt()
            }

        return "Solution: $result"
    }
}