import Day5.Maps.*
import kotlinx.coroutines.*

class Day5 : AoC("day5") {
    override fun getFirstSolution(): String {
        val (seeds, maps) = rawInputData.parseInput()

        val seedLocations = seeds.map { it.walkThroughMapping(maps) }

        return "Solution: ${seedLocations.min()}"
    }

    override fun getSecondSolution(): String {
        val (seedRangeInputData, maps) = rawInputData.parseInput()

        val seedRanges = seedRangeInputData.parse()

        val result = runBlocking {
            val minimumLocationPerSeedRange = seedRanges.map { (seedRangeStart, seedRangeLength) ->
                async(Dispatchers.Default) {
                    (seedRangeStart..seedRangeStart + seedRangeLength)
                        .minOf { seed -> seed.walkThroughMapping(maps) }
                }
            }

            return@runBlocking minimumLocationPerSeedRange.awaitAll()
                .min()
        }

        return "Solution is $result"
    }

    private fun List<Long>.parse(): List<Pair<Long, Long>> {
        val rangeStarts = this.filterIndexed { index, _ -> index.mod(2) == 0 }
        val rangeLengths = this.filterIndexed { index, _ -> index.mod(2) == 1 }

        return rangeStarts.zip(rangeLengths)
    }

    private fun Long.walkThroughMapping(maps: Map<Maps, List<RangeMaps>>) =
        MAPPING_PATH.map { maps[it]!!.buildMappingOperation() }
            .fold(this) { currentValue, operation -> operation(currentValue) }

    private fun List<RangeMaps>.buildMappingOperation(): (Long) -> Long  = { inputNumber: Long ->
        val associatedRange = firstOrNull { rangeInfo ->
            inputNumber in (rangeInfo.sourceStart..rangeInfo.sourceStart + rangeInfo.length)
        }

        associatedRange?.let { rangeInfo ->
            rangeInfo.targetStart + inputNumber - rangeInfo.sourceStart
        } ?: inputNumber
    }

    private fun List<String>.parseInput(): Pair<List<Long>, Map<Maps, List<RangeMaps>>> {
        val seeds = this.first { it.contains("seeds:") }
            .substringAfter(":")
            .split(' ')
            .filter { it.isNotBlank() }
            .map { it.toLong() }

        val maps = Maps.entries.associateWith { mapCategory ->
            val inputStartIndex = this.indexOfFirst { it.contains(mapCategory.inputFileIdentifier) }
            val inputLength = this.subList(fromIndex = inputStartIndex, toIndex = this.lastIndex)
                .indexOfFirst { it.isEmpty() }

            val inputData = this.subList(
                fromIndex = inputStartIndex + 1,
                toIndex = if(inputLength == -1) this.lastIndex + 1 else inputStartIndex + inputLength
            )

            return@associateWith inputData.map { inputLine ->
                val splitted = inputLine.split(' ')
                    .filter { it.isNotBlank() }
                    .map { it.toLong() }

                return@map RangeMaps(
                    sourceStart = splitted[1],
                    targetStart = splitted[0],
                    length = splitted[2]
                )
            }
        }

        return seeds to maps
    }

    data class RangeMaps(
        val sourceStart: Long,
        val targetStart: Long,
        val length: Long
    )

    enum class Maps(val inputFileIdentifier: String) {
        SEED_TO_SOIL("seed-to-soil map"),
        SOIL_TO_FERTI("soil-to-fertilizer map"),
        FERTI_TO_WATER("fertilizer-to-water map"),
        WATER_TO_LIGHT("water-to-light map"),
        LIGHT_TO_TEMP("light-to-temperature map"),
        TEMP_TO_HUMID("temperature-to-humidity map"),
        HUMID_TO_LOCAL("humidity-to-location map"),
    }

    companion object {
        private val MAPPING_PATH = listOf(
            SEED_TO_SOIL,
            SOIL_TO_FERTI,
            FERTI_TO_WATER,
            WATER_TO_LIGHT,
            LIGHT_TO_TEMP,
            TEMP_TO_HUMID,
            HUMID_TO_LOCAL
        )
    }
}