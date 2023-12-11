class Day11 : AoC("day11") {
    val GALAXIES : List<Galaxy>

    init {
        GALAXIES = rawInputData.getGalaxies()
    }

    override fun getFirstSolution(): String {
        return "Solution: ${calculateDistance(scaleFactor = 2)}"
    }

    override fun getSecondSolution(): String {
        return "Solution: ${calculateDistance(scaleFactor = 1000000)}"
    }

    private fun calculateDistance(scaleFactor: Int): Long {
        return GALAXIES.indices.getPairs()
            .sumOf { (i, j) -> GALAXIES[i].distanceTo(GALAXIES[j], scaleFactor) }
    }

    private fun List<String>.getGalaxies(): List<Galaxy> {
        return this.mapIndexed { rowIndex, line ->
            line.mapIndexed { colIndex, char ->
                if (char == '#') Pair(rowIndex, colIndex) else null
            }
        }
            .flatten()
            .filterNotNull()
            .map { (row, col) -> Galaxy(posX = col, posY = row) }
    }

    private fun getEmptyCols(): Set<Int> = rawInputData.first().indices
        .filter { colIndex ->
            GALAXIES.all { it.posX != colIndex }
        }.toSet()

    private fun getEmptyRows(): Set<Int> = rawInputData.indices
        .filter { rowIndex ->
            GALAXIES.all { it.posY != rowIndex }
        }.toSet()

    private fun IntRange.getPairs(): List<Pair<Int, Int>> = this.flatMap { i ->
            (i + 1 .. this.max()).map { j -> Pair(i, j) }
        }

    private fun Galaxy.distanceTo(other: Galaxy, scaleFactor: Int): Long {
        val dx = this.posX.getCoordinateDistance(
            end = other.posX,
            scaleFactor = scaleFactor,
            scaleIndices = getEmptyCols()
        )

        val dy = this.posY.getCoordinateDistance(
            end = other.posY,
            scaleFactor = scaleFactor,
            scaleIndices = getEmptyRows()
        )

        return dx + dy
    }

    private fun Int.getCoordinateDistance(end: Int, scaleIndices: Set<Int>, scaleFactor: Int): Long {
        val (smaller, larger) = listOf(this, end).sorted()
        val range = (smaller + 1 .. larger).toList()

        return (range.size + (scaleFactor - 1) * range.intersect(scaleIndices).size).toLong()
    }

    data class Galaxy(
        val posX: Int,
        val posY: Int,
    )
}