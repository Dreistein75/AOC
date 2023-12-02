class Day2 : AoC("day2") {
    override fun getFirstSolution(): String {
        val result = rawInputData.map { it.parseInputLine() }
            .filter { it.shownSets.all { shownSet -> shownSet.isPossible() } }
            .sumOf { it.gameId }

        return "Solution is $result"
    }

    override fun getSecondSolution(): String {
        val result = rawInputData.map { it.parseInputLine() }
            .map { it.calculateMinimumSet() }
            .sumOf { it.red * it.green * it.blue }

        return "Solution is $result"
    }


    private fun String.parseInputLine(): GameData {
        val shownSetsOfGame = this.substringAfter(": ").split(';')

        return GameData(
            gameId = this.extractGameId()!!,
            shownSets = shownSetsOfGame.map { it.parseRGB() }
        )
    }

    private fun String.extractGameId() = this.extractIntAccordingToRegex(regex = "Game (\\d+):".toRegex())

    private fun String.parseRGB(): SetData {
        val redRegex = "(\\d+) red".toRegex()
        val greenRegex = "(\\d+) green".toRegex()
        val blueRegex = "(\\d+) blue".toRegex()

        return SetData(
            red = this.extractIntAccordingToRegex(redRegex) ?: 0,
            green = this.extractIntAccordingToRegex(greenRegex) ?: 0,
            blue = this.extractIntAccordingToRegex(blueRegex) ?: 0
        )
    }

    private fun String.extractIntAccordingToRegex(regex: Regex): Int? {
        return regex.find(this)?.groupValues?.get(1)?.toInt()
    }

    data class SetData(
        val red: Int,
        val green: Int,
        val blue: Int,
    ) {
        fun isPossible() = red <= 12 && green <= 13 && blue <= 14
    }

    data class GameData(
        val gameId: Int,
        val shownSets: List<SetData>
    ) {
        fun calculateMinimumSet() = SetData(
            red = this.shownSets.maxOf { it.red },
            green = this.shownSets.maxOf { it.green },
            blue = this.shownSets.maxOf { it.blue }
        )
    }
}