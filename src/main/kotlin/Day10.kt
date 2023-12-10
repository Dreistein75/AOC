import Day10.Direction.*
import Day10.Tile
import Day10.TileType.*
import kotlin.math.min

class Day10 : AoC("day10") {
    private val GRID: Grid
    private val LOOP_START: Tile

    init {
        GRID = rawInputData.parse()
        LOOP_START = GRID.firstNotNullOf { line -> line.firstOrNull { it.type == START } }
    }

    override fun getFirstSolution(): String {
        val mainLoop = extractMainLoop()

        return "Solution: ${mainLoop.size/2}"
    }

    override fun getSecondSolution(): String {
        val mainLoop = extractMainLoop()

        val result = GRID.sumOf { line -> line.count { tile -> tile.isInnerTile(mainLoop) } }

        return "Solution: $result"
    }

    private fun extractMainLoop(): List<Tile> {
        val oneNeighborOfStart = LOOP_START.getNeighbors()
            .first { it.getNeighbors().contains(LOOP_START) }

        return generateSequence(Pair(LOOP_START, oneNeighborOfStart)) { (previousPile, currentPile) ->
            val nextPile = currentPile.getNeighbors().first { it != previousPile }
            Pair(currentPile, nextPile)
        }
            .map { it.second }
            .takeWhile { it != LOOP_START }
            .toList()
            .plus(LOOP_START)
    }

    private fun Tile.go(direction: Direction): Tile? {
        val col = this.col + direction.dx
        val row = this.row + direction.dy

        return if (min(col, row) < 0 ) null else getTile(row, col)
    }

    private fun Tile.getNeighbors(): Set<Tile> = ( this.type.directions?.mapNotNull { this.go(it) }
            ?: emptyList() )
            .toSet()

    private fun List<String>.parse(): Grid =
        this.mapIndexed { lineIndex, line ->
            line.mapIndexed { colIndex, symbol ->
                Tile(row = lineIndex, col = colIndex, type = TileType.fromChar(symbol))
            }
        }

    private fun getTile(row: Int, col: Int) = GRID[row][col]

    private fun Tile.getTileShape(): TileType {
        if (this.type != START) return this.type

        val startNeighbors = LOOP_START.getNeighbors()
            .filter { it.getNeighbors().contains(LOOP_START) }
            .toSet()

        return TileType.entries.firstOrNull {
            startNeighbors ==
                (it.directions?.mapNotNull { direction -> LOOP_START.go(direction) } ?: emptyList()).toSet()
        }!!
    }

    private fun Tile.isInnerTile(loop: List<Tile>): Boolean {
        if (loop.contains(this)) return false

        fun Int.isOdd(): Boolean = this % 2 == 1

        val numberOfLoopVerticalsLeft = (0 until col).count { colIndex ->
            val tempTile = getTile(row, colIndex)
            return@count tempTile.type == VERTICAL && tempTile in loop
        }

        fun countNumberOfLoopLightningsLeft(lightningPartA: TileType, lightningPartB: TileType): Int {
            fun Tile.getNextLightningTileInLine(): Tile {
                val indicesOfVerticalPipes = (this.col + 1 until this@isInnerTile.col - 1)
                    .takeWhile { getTile(row, it).type == HORIZONTAL }

                return indicesOfVerticalPipes.maxOrNull()?.let { getTile(row, it+1) }
                    ?: getTile(row, this.col + 1)
            }

            return (0 until col-1).count { colIndex ->
                val tileA = getTile(row, colIndex)
                if (tileA.getTileShape() != lightningPartA) return@count false
                if (tileA !in loop) return@count false

                val tileB = tileA.getNextLightningTileInLine()
                return@count tileB.getTileShape() == lightningPartB
            }
        }

        return (numberOfLoopVerticalsLeft +
            countNumberOfLoopLightningsLeft(lightningPartA = NW, lightningPartB = SE) +
            countNumberOfLoopLightningsLeft(lightningPartA = SW, lightningPartB = NE)
            ).isOdd()
    }

    enum class TileType(val directions: Set<Direction>?, val symbol: Char) {
        VERTICAL(directions = setOf(UP, DOWN), symbol = '|'),
        HORIZONTAL(directions = setOf(LEFT, RIGHT), symbol = '-'),
        SW(directions = setOf(UP, RIGHT), symbol = 'L'),
        SE(directions = setOf(UP, LEFT), symbol = 'J'),
        NE(directions = setOf(LEFT, DOWN), symbol = '7'),
        NW(directions = setOf(RIGHT, DOWN), symbol = 'F'),
        EMPTY(directions = null, symbol = '.'),
        START(directions = Direction.entries.toSet(), symbol = 'S');

        companion object {
            private val map = entries.associateBy(TileType::symbol)
            fun fromChar(input: Char): TileType = map[input]!!
        }
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
        val type: TileType
    )
}

typealias Grid = List<List<Tile>>