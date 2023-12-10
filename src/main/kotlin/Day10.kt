import Day10.Direction.*
import Day10.Tile
import kotlin.math.min

class Day10 : AoC("day10") {
    private val GRID: Grid
    private val START: Tile

    init {
        GRID = rawInputData.parse()
        START = GRID.firstNotNullOf { line -> line.firstOrNull { it.tile == 'S' } }
    }

    override fun getFirstSolution(): String {
        val wholePath = extractMainLoop()

        return "Solution: ${wholePath.size/2}"
    }

    private fun extractMainLoop(): List<Tile> {
        val oneNeighborOfStart = Direction.entries
            .mapNotNull { START.go(it) }
            .first { it.getNeighbors().contains(START) }

        return generateSequence(Pair(START, oneNeighborOfStart)) { (previousPile, currentPile) ->
                val nextPile = currentPile.getNeighbors().first { it != previousPile }
                Pair(currentPile, nextPile)
            }
            .map { it.second }
            .takeWhile { it != START }
            .toList()
            .plus(START)
    }

    private fun Tile.go(direction: Direction): Tile? {
        val col = this.col + direction.dx
        val row = this.row + direction.dy

        return if (min(col, row) < 0 ) null else getTile(row, col)
    }

    private fun Tile.getNeighbors(): Set<Tile> =
        TILE_MAP[this.tile]?.mapNotNull { this.go(it) }?.toSet() ?: emptySet()

    private fun List<String>.parse(): Grid =
        this.mapIndexed { lineIndex, line ->
            line.mapIndexed { colIndex, symbol -> Tile(row = lineIndex, col = colIndex, tile = symbol) }
        }

    private fun getTile(row: Int, col: Int) = GRID[row][col]

    companion object {
        private val TILE_MAP = mapOf(
            '|' to setOf(UP, DOWN),
            '-' to setOf(LEFT, RIGHT),
            'L' to setOf(UP, RIGHT),
            'J' to setOf(UP, LEFT),
            '7' to setOf(LEFT, DOWN),
            'F' to setOf(RIGHT, DOWN),
            '.' to null,
            'S' to emptySet(),
        )
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0,1),
        LEFT(-1,0),
        RIGHT(1,0)
    }

    data class Tile(
        val row: Int,
        val col: Int,
        val tile: Char
    )

}

typealias Grid = List<List<Tile>>