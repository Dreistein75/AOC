class Day6 : AoC("day6") {
    override fun getFirstSolution(): String {
        val raceData = rawInputData.extractRaceData {
            this.substringAfter(":")
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.toLong() }
        }

        val result = raceData
            .map { it.calculatePossibleWins() }
            .fold(1) { acc, possibilities -> acc * possibilities}

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val raceData = rawInputData.extractRaceData {
            listOf(
                this.substringAfter(":")
                    .replace("\\s".toRegex(), "")
                    .toLong()
            )
        }

        val result = raceData.first().calculatePossibleWins()

        return "Solution is $result"
    }

    private fun List<String>.extractRaceData(inputParsing: String.() -> List<Long>): List<RaceData> {
        val times = this.first().inputParsing()
        val recordDistance = this.last().inputParsing()

        return times.zip(recordDistance).map { it.toRaceData() }
    }

    private fun RaceData.calculatePossibleWins(): Int {
        fun Long.calculateDistance(): Long {
            val remainingRaceTime = time - this
            return remainingRaceTime * this
        }

        val winningStrategies = (1 until this.time)
            .filter { it.calculateDistance() > this.record }

        return winningStrategies.size
    }

    data class RaceData(
        val time: Long,
        val record: Long,
    )

    private fun Pair<Long, Long>.toRaceData(): RaceData = RaceData(time = this.first, record = this.second)
}