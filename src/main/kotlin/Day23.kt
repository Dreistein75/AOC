import Day23.Type.*
import Day23.Configuration
import Day23.Direction.*

class Day23 : AoC("day23") {
    private val GRID: Grid

    init {
        GRID = rawInputData.parseGrid()
    }

    override fun getFirstSolution(): String {
        val result = getAllPaths()
            .maxOfOrNull { it.way.size }

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val result = getAllPaths(ex2 = true)
            .maxOfOrNull { it.way.size }

        return "Solution: $result"
    }

    private fun List<String>.parseGrid(): Grid {
        return Grid(
            rows = this.map { line ->
                line.map { char -> Type.fromChar(char) }
                },
            start = Coordinates(this.first().indexOfFirst { it == '.' }, 0),
            finish = Coordinates(this.last().indexOfFirst { it == '.' }, this.lastIndex),
        )
    }

    private fun getAllPaths(ex2: Boolean = false): List<Path> {
        val initial = Path(listOf(Configuration(
            currentPosition = GRID.start,
            lastDirection = DOWN
        )))

        return generateSequence(listOf(initial)) {
            it.flatMap { path ->
                if (path.finished) listOf(path) else {
                    val continues = path.getNextPossibleSteps(ex2)
                    return@flatMap continues.map { nextStep ->
                        Path(
                            way = path.way.plus(nextStep),
                            finished = nextStep.currentPosition == GRID.finish
                        )
                    }
                }
            }
        }.takeWhile { it.any { !it.finished } }.last()
    }

    private fun Path.getNextPossibleSteps(ex2: Boolean): List<Configuration> {
        if (this.finished) return emptyList()

        val endOfPath = this.way.last()
        val posDir = getPossibleDirections(GRID.get(endOfPath.currentPosition), ex2)
            .minus(endOfPath.lastDirection.opposite())

        return posDir.map { direction -> endOfPath.currentPosition.goTo(direction) to direction }
            .filter { GRID.contains(it.first) }
            .filter { GRID.get(it.first) != BLOCKED }
            .map { Configuration(currentPosition = it.first, lastDirection = it.second) }
    }

    private fun getPossibleDirections(type: Type, ex2: Boolean): List<Direction> {
        return if (ex2) Direction.entries else when (type) {
            FREE -> Direction.entries
            FORCE_RIGHT -> listOf(RIGHT)
            FORCE_LEFT -> listOf(LEFT)
            FORCE_UP -> listOf(UP)
            FORCE_DOWN -> listOf(DOWN)
            else -> error("woops")
        }
    }

    enum class Type(val symbol: Char) {
        BLOCKED('#'),
        FREE('.'),
        FORCE_RIGHT('>'),
        FORCE_LEFT('<'),
        FORCE_UP('^'),
        FORCE_DOWN('v'),
        ;

        companion object {
            private val map = entries.associateBy { it.symbol }
            fun fromChar(char: Char): Type = map[char]!!
        }
    }

    data class Grid(
        val rows: List<List<Type>>,
        val start: Coordinates,
        val finish: Coordinates,
    ) {
        fun get(coordinates: Coordinates): Type = with(coordinates) {
            rows[y][x]
        }

        fun contains(coordinates: Coordinates): Boolean =
            coordinates.x in rows.first().indices && coordinates.y in rows.indices
    }

    data class Configuration(
        val currentPosition: Coordinates,
        val lastDirection: Direction
    )

    data class Coordinates(
        val x: Int,
        val y: Int,
    ) {
        fun goTo(direction: Direction) =
            Coordinates(x + direction.dx, y + direction.dy)
    }

    data class Path(
        val way: List<Configuration>,
        val finished: Boolean = false
    )

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0,1),
        LEFT(-1,0),
        RIGHT(1,0),
        ;

        fun opposite(): Direction = when(this) {
            UP -> DOWN
            DOWN -> UP
            RIGHT -> LEFT
            LEFT -> RIGHT
        }
    }
}