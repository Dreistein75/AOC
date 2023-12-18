import Day18.Direction.*
import java.lang.RuntimeException

class Day18 : AoC("day18") {
    override fun getFirstSolution(): String {
        val result = rawInputData.parse().countEnclosingSquares()

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val result = rawInputData.parse().correctParsing().countEnclosingSquares()

        return "Solution: $result"
    }

    private fun List<String>.parse(): List<DigInstruction> {
        val pattern = """([LRDU])\s+(\d+)\s+\(#([0-9a-f]+)\)""".toRegex()

        fun Char.toDirection(): Direction {
            return Direction.entries.first { it.name.first() == this }
        }

        return this.map {
            val (dirChar, len, color) = pattern.find(it)!!.destructured
            DigInstruction(
                direction = dirChar.single().toDirection(),
                length = len.toInt(),
                color = color
            )
        }
    }

    private fun List<DigInstruction>.correctParsing(): List<DigInstruction> {
        fun Char.toDirection(): Direction = when(this) {
            '0' -> RIGHT
            '1' -> DOWN
            '2' -> LEFT
            '3' -> UP
            else -> throw RuntimeException()
        }

        return this.map { DigInstruction(
            direction = it.color.last().toDirection(),
            length = it.color.substring(0, it.color.length - 1).toInt(16),
            color = it.color    // irrelevant I guess
        ) }
    }

    private fun List<DigInstruction>.countEnclosingSquares(): Long {
        val digLoop = this.runningFold(Coordinates(0,0)) { acc, instruction ->
            acc.go(instruction.direction, instruction.length)
        }

        val loopLength = this.sumOf { it.length.toLong() }

        return digLoop.countInteriorPoints(loopLength) + loopLength
    }

    private fun List<Coordinates>.countInteriorPoints(borderPoints: Long): Long {
        require(this.first() == this.last())

        val areaViaGauss = this.dropLast(1)
            .zip(this.subList(1, this.size))
            .fold(0L) { acc, (vertex, nextVertex) ->
                acc + (vertex.x * nextVertex.y - vertex.y * nextVertex.x)
            } / 2

        // Note: Integer division by 2 above falsifies the "true" area by 0.5. However,
        // this function returns the area minus something X which gets also divided by 2
        // and if the area was not even, then so was X, i.e. the error cancels out there

        // Pick's Theorem:
        return areaViaGauss + 1 - borderPoints/2
    }

    data class DigInstruction(
        val direction: Direction,
        val length: Int,
        val color: String,
    )

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        DOWN(0,1),
        LEFT(-1,0),
        RIGHT(1,0),
        ;
    }

    data class Coordinates(
        val x: Long,
        val y: Long,
    ) {
        fun go(dir: Direction, length: Int) =
            Coordinates(x + dir.dx * length, y + dir.dy * length)
    }
}