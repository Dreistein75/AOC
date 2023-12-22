import Day21.Type.*

class Day21 : AoC("day21") {
    override fun getFirstSolution(): String {
        val grid = rawInputData.parseGrid()
        val startPosition = rawInputData.findStart()

        val result = (1 .. 64).fold(listOf(startPosition)) { acc, i ->
            if (i % 100 == 0) { println( i * 100.0 / 26501365.0) }
            acc.flatMap { currentPosition ->
                Direction.entries.map { currentPosition.goTo(it) }
                    .filter { grid.getType(it) == FREE }
            }.distinct()
        }.size

        return "Solution: $result"
    }

    private fun List<String>.parseGrid(): Grid = this.map { line ->
        line.map { if (it == '#') BLOCKED else FREE }
    }.toGrid()

    private fun List<List<Type>>.toGrid() = Grid(this)

    private fun List<String>.findStart(): Coordinates {
        val rowIndex = this.indexOfFirst { it.contains('S') }

        return Coordinates(
            y = rowIndex,
            x = this[rowIndex].indexOf('S')
        )
    }

    data class Grid(
        val rows: List<List<Type>>
    ) {
        fun getType(coordinates: Coordinates): Type = with(coordinates) {
            rows[y.modulo(rows.size)][x.modulo(rows.first().size)]
        }

        private tailrec fun Int.modulo(modulus: Int): Int {
            return if (this > 0) this % modulus else (this + modulus).modulo(modulus)
        }
    }

    enum class Type {
        BLOCKED,
        FREE,
    }

    data class Coordinates(
        val x: Int,
        val y: Int,
    ) {
        fun goTo(direction: Direction) =
            Coordinates(x + direction.dx, y + direction.dy)
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0,1),
        LEFT(-1,0),
        RIGHT(1,0)
    }
}