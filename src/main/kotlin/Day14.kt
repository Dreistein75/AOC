import Day14.Field
import Day14.Field.*

class Day14 : AoC("day14") {
    override fun getFirstSolution(): String {
        val result = rawInputData.parse()
            .tiltNorth()
            .calculateLoad()

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val result = rawInputData.parse()
            .spin(1000000000)
            .calculateLoad()

        return "Solution: $result"
    }

    private fun List<String>.parse(): RockGrid = (0 until this.first().length).map { colIndex ->
        this.map { row -> Field.fromChar(row[colIndex])!! }
    }

    private fun List<Field>.getLoad(): Int = this.mapIndexed { idx, content ->
        if (content == MOVEABLE) this.size - idx else 0
    }.sum()

    private fun RockGrid.calculateLoad() : Int = this.sumOf { it.getLoad() }

    private fun RockGrid.spinOnce(): RockGrid = this.tiltNorth().tiltWest().tiltSouth().tiltEast()

    private fun RockGrid.tiltWest(): RockGrid = this.transpose().tiltNorth().transpose()
    private fun RockGrid.tiltSouth(): RockGrid = this.reversed().tiltNorth().reversed()
    private fun RockGrid.tiltEast(): RockGrid = this.transpose().tiltSouth().transpose()
    private fun RockGrid.tiltNorth(): RockGrid = this.map { it.dipNorth() }

    private fun List<Field>.dipNorth(): List<Field> {
        return this.split(separator = FIXED)
            .map { it.sortedWith(Field.comparator) }
            .fold(emptyList()) { acc, it -> acc + it }
    }

    private fun RockGrid.reversed(): RockGrid = this.map { it.reversed() }

    private fun<T> List<List<T>>.transpose(): List<List<T>> =
        this.first().indices.map { colIndex ->
            this.map { it[colIndex] }
        }

    private fun<T> List<T>.split(separator: T): List<List<T>> {
        return this.fold(listOf(emptyList())) { acc, item ->
            when {
                item == separator -> acc.plus(element = listOf(separator))
                acc.last() == listOf(separator) -> acc.plus(element = listOf(item))
                else -> acc.dropLast(1) + listOf(acc.last() + item)
            }
        }
    }

    private fun RockGrid.spin(times: Int): RockGrid {
        return with(this.findCycleInSpinningSequence()){
            val stepsLeftToGo = (times - cycleStartIndex) % cycleLength

            (1 .. stepsLeftToGo).fold(cycleStart) { acc, _ -> acc.spinOnce() }
        }
    }

    private fun RockGrid.findCycleInSpinningSequence(): SpinCycleInfo {
        tailrec fun recursiveSpin(
            current: RockGrid, index: Int, indexMap: Map<RockGrid, Int>
        ): SpinCycleInfo {
            return indexMap[current]?.let {
                SpinCycleInfo(current, it, index - it)
            } ?: recursiveSpin(current.spinOnce(), index + 1, indexMap + (current to index))
        }

        return recursiveSpin(this, 0, emptyMap())
    }

    data class SpinCycleInfo(
        val cycleStart: RockGrid,
        val cycleStartIndex: Int,
        val cycleLength: Int
    )

    enum class Field(val symbol: Char) {
        EMPTY('.'),
        FIXED('#'),
        MOVEABLE('O');

        companion object {
            private val map = entries.associateBy(Field::symbol)
            fun fromChar(symbol: Char): Field? = map[symbol]

            val comparator: Comparator<Field> = Comparator { o1, o2 ->
                when (o1) {
                    o2 -> 0
                    MOVEABLE -> -1
                    else -> 1
                }
            }
        }
    }
}

typealias RockGrid = List<List<Field>>