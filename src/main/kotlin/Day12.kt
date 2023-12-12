import Day12.Spring
import Day12.Spring.BROKEN
import Day12.Spring.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class Day12 : AoC("day12") {
    override fun getFirstSolution(): String {
        val inputData = rawInputData.map { it.parseLine() }

        return "Solution: " + inputData.sumOf { it.arrangement.countPossibleFixes(it.brokenSprings) }
    }

    override fun getSecondSolution(): String {
        val inputData = rawInputData.unfold(5).map { it.parseLine() }

        val result = runBlocking {
            val partialResult = inputData.map {
                async(Dispatchers.Default) {
                    it.arrangement.countPossibleFixes(it.brokenSprings)
                }
            }

            return@runBlocking partialResult.awaitAll()
        }

        return "Solution: " + result.sum()
    }

    private fun List<String>.unfold(times: Int): List<String> {
        return this.map { line ->
            val (left, right) = line.split(' ')
            val arrangement = List(times) { left }.joinToString(separator = "?")
            val brokenSprings = List(times) { right }.joinToString(separator = ",")

            return@map "$arrangement $brokenSprings"
        }
    }

    private fun String.parseLine(): DataLine {
        val (corruptPart, groupInfo) = this.split(' ')

        return DataLine(
            brokenSprings = groupInfo.split(',').map { it.toInt() },
            arrangement = corruptPart.replace(Regex("\\\\.+"), ".")
                .trim { it == '.' }
                .map { Spring.fromChar(it) }
        )
    }

    private fun List<Spring>.countPossibleFixes(groupInfo: List<Int>): Long {
        if (this.isEmpty()) {
            return if (groupInfo.isEmpty()) 1 else 0
        }

        val cacheKey = this to groupInfo

        if (CACHE.containsKey(cacheKey)) {
            return CACHE[cacheKey]!!
        }

        val result = when(this.first()) {
            WORKS -> this.dropFirst().countPossibleFixes(groupInfo)
            DUNNO -> listOf(WORKS, BROKEN).sumOf {
                this.replaceFirstWith(it).countPossibleFixes(groupInfo)
            }
            BROKEN -> {
                val lengthOfBrokenStreak = groupInfo.firstOrNull() ?: return 0

                return when {
                    this.size < lengthOfBrokenStreak -> 0
                    this.subList(0, lengthOfBrokenStreak).any { it == WORKS } -> 0
                    this.size == lengthOfBrokenStreak -> if (groupInfo.size == 1) 1 else 0
                    this[lengthOfBrokenStreak] == BROKEN -> return 0
                    else -> this.subList(lengthOfBrokenStreak + 1, this.size)
                        .countPossibleFixes(groupInfo.dropFirst())
                }
            }
        }

        CACHE[cacheKey] = result

        return result
    }

    private fun List<Spring>.replaceFirstWith(spring: Spring): List<Spring> =
        listOf(spring).plus(this.dropFirst())

    private fun<T> List<T>.dropFirst(): List<T> = this.subList(1, this.size)

    data class DataLine(
        val brokenSprings: List<Int>,
        val arrangement: List<Spring>
    )

    companion object {
        private val CACHE = mutableMapOf<Pair<List<Spring>, List<Int>>, Long>()
    }

    enum class Spring(val symbol: Char) {
        WORKS('.'),
        BROKEN('#'),
        DUNNO('?');

        companion object {
            private val map = entries.associateBy(Spring::symbol)
            fun fromChar(input: Char): Spring = map[input]!!
        }
    }
}