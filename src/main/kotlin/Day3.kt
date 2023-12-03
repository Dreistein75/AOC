import kotlin.math.max
import kotlin.math.min

class Day3 : AoC("day3") {
    override fun getFirstSolution(): String {
        val allNumbers = rawInputData.extractElementsWithPosition(regex = "\\d+".toRegex())

        val result = allNumbers.filter { it.isMotorPart() }
            .sumOf { it.content.toInt() }

        return "Solution is $result"
    }


    override fun getSecondSolution(): String {
        val allNumbers = rawInputData.extractElementsWithPosition(regex = "\\d+".toRegex())
        val potentialGears = rawInputData.extractElementsWithPosition(regex = "\\*".toRegex())

        val result = potentialGears.map { it.getAdjacentNumbers(allNumbers) }
            .filter { it.size == 2 }
            .sumOf { it.first() * it.last() }

        return "Solution is $result"
    }

    private fun List<String>.extractElementsWithPosition(regex: Regex): List<ElementWithPosition> {
        return this.mapIndexed { lineNumber, lineContent ->
            lineContent.extractElementsWithLinePosition(regex)
                .map { (elementContent, linePosition) ->
                    ElementWithPosition(elementContent, line = lineNumber, index=linePosition)
                }
        }.flatten()
    }

    private fun String.extractElementsWithLinePosition(regex: Regex): List<Pair<String, Int>> =
        regex.findAll(this).toList().map { match -> match.value to match.range.first }

    private fun ElementWithPosition.isMotorPart(): Boolean {
        return this.getBoundaryIndices()
            .any { (line, pos) -> rawInputData[line][pos] != '.' }
    }

    private fun ElementWithPosition.getBoundaryIndices(): List<Pair<Int, Int>> {
        val endIndex = index + content.length - 1

        val upperBoundary = buildHorizontalBoundaryIndices(line = line - 1, start = index - 1, end = endIndex + 1)
        val lowerBoundary = buildHorizontalBoundaryIndices(line = line + 1, start = index - 1, end = endIndex + 1)
        val leftBoundary = buildVerticalBoundary(position = index - 1, start = line - 1, end = line + 1)
        val rightBoundary = buildVerticalBoundary(position = endIndex + 1, start = line - 1, end = line + 1)

        return upperBoundary + lowerBoundary + leftBoundary + rightBoundary
    }

    private fun buildHorizontalBoundaryIndices(line: Int, start: Int, end: Int): List<Pair<Int, Int>> {
        return if (line in rawInputData.indices) {
            (max(start, 0) .. min(end, rawInputData[line].lastIndex))
                .toList()
                .map { line to it }
        } else emptyList()
    }

    private fun buildVerticalBoundary(position: Int, start: Int, end: Int): List<Pair<Int, Int>> {
        return if (position in rawInputData.first().indices) {
            (max(start, 0) .. min(end, rawInputData.lastIndex))
                .map { it to position }
        } else emptyList()
    }

    private fun ElementWithPosition.getAdjacentNumbers(numbers: List<ElementWithPosition>): List<Int> {
        return this.getBoundaryIndices()
            .mapNotNull { position -> numbers.getElementAtPosition(position) }
            .distinct()
            .map { it.content.toInt() }
    }

    private fun List<ElementWithPosition>.getElementAtPosition(position: Pair<Int, Int>): ElementWithPosition? {
        val (line, columnIndex) = position

        return this.filter { element -> element.line == line }
            .firstOrNull {
                element -> columnIndex in (element.index until element.index + element.content.length)
            }
    }

    data class ElementWithPosition(
        val content: String,
        val line: Int,
        val index: Int,
    )
}