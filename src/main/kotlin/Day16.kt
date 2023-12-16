import Day16.Configuration
import Day16.Direction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class Day16 : AoC("day16") {
    val GRID: Grid

    init {
        GRID = rawInputData.parseGrid()
    }

    override fun getFirstSolution(): String {
        val initialConfig = Configuration(
            currentPosition = Coordinates(0,0),
            lastDirection = RIGHT
        )

        val result = initialConfig.countIlluminatedTiles()

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val result = runBlocking {
            val singleResults = buildAllStartConfigurations().map {
                async(Dispatchers.Default) {
                    it.countIlluminatedTiles()
                }
            }.awaitAll()

            return@runBlocking singleResults.max()
        }

        return "Solution: $result"
    }

    private fun Configuration.countIlluminatedTiles(): Int {
        val allLightBeams = this.getAllBeams()

        return allLightBeams.flatten().map { it.currentPosition }.toSet().size
    }

    private fun Configuration.getAllBeams(loopBreaker: Set<Configuration> = emptySet()): List<Beam> {
        val beamOfLight = generateSequence(listOf(this)) {
            it.firstOrNull()?.getNextTiles()
        }.takeWhile { it.isNotEmpty() && !loopBreaker.contains(it.first()) }.distinctBy{ it.first() }.toList()

        val mainBeam = beamOfLight.map { it.first() }
        val branchStarts = beamOfLight.filter { it.size > 1 }
            .map { it.last() }

        val amendedLoopBreaker = loopBreaker.plus(mainBeam)

        val branches = branchStarts.flatMap { it.getAllBeams(amendedLoopBreaker) }

        return listOf(mainBeam).plus(branches)
    }

    private fun buildAllStartConfigurations(): List<Configuration> {
        val (maxX, maxY) = GRID.getDimension()

        val horizontalBeams = (0 .. maxY).flatMap { row ->
            listOf(
                Configuration(Coordinates(0, row), RIGHT),
                Configuration(Coordinates(maxX, row), LEFT)
            )
        }
        val verticalBeams = (0 .. maxX).flatMap { column ->
            listOf(
                Configuration(Coordinates(column, 0), DOWN),
                Configuration(Coordinates(column, maxY), UP)
            )
        }

        return horizontalBeams + verticalBeams
    }

    private fun List<String>.parseGrid() = Grid(rows = this.map { line ->
            line.map { Tile.fromChar(it) }
        }
    )

    private fun Configuration.getNextTiles(): List<Configuration> {
        val directionsToNextTiles = GRID.get(currentPosition).applyChangeDirection(lastDirection)
            .filter { direction -> GRID.contains(currentPosition.goTo(direction)) }

        return directionsToNextTiles.map { direction ->
            Configuration(
                currentPosition = currentPosition.goTo(direction),
                lastDirection = direction
            )
        }
    }

    private fun Coordinates.goTo(direction: Direction) =
        Coordinates(x + direction.dx, y + direction.dy)


    data class Configuration(
        val currentPosition: Coordinates,
        val lastDirection: Direction
    )

    data class Grid(
        val rows: List<List<Tile>>
    ) {
        fun contains(coordinates: Coordinates): Boolean =
            coordinates.x in rows.first().indices && coordinates.y in rows.indices

        fun get(coordinates: Coordinates): Tile = with(coordinates) {
            rows[y][x]
        }

        fun getDimension(): Pair<Int, Int> = rows.first().lastIndex to rows.lastIndex
    }

    data class Coordinates(
        val x: Int,
        val y: Int,
    )

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0,1),
        LEFT(-1,0),
        RIGHT(1,0)
    }

    enum class Tile(val symbol: Char, val applyChangeDirection: (Direction) -> List<Direction>) {
        EMPTY('.', { dir -> listOf(dir) }),
        BACK_MIRROR('\\', { dir -> val res = when(dir) {
                UP -> LEFT
                DOWN -> RIGHT
                LEFT -> UP
                RIGHT -> DOWN
        }
            listOf(res)
        }),
        MIRROR('/', { dir -> val res = when(dir) {
            UP -> RIGHT
            DOWN -> LEFT
            LEFT -> DOWN
            RIGHT -> UP
        }
            listOf(res)
        }),
        V_SPLIT('|', { dir -> when(dir) {
            UP, DOWN -> listOf(dir)
            LEFT, RIGHT -> listOf(UP, DOWN)
        }}),
        H_SPLIT('-', { dir -> when(dir) {
            UP, DOWN -> listOf(LEFT, RIGHT)
            LEFT, RIGHT -> listOf(dir)
        }})
        ;

        companion object {
            private val map = entries.associateBy(Tile::symbol)
            fun fromChar(symbol: Char): Tile = map[symbol]!!
        }
    }
}

typealias Beam = List<Configuration>