import java.lang.RuntimeException

class Day4 : AoC("day4") {
    override fun getFirstSolution(): String {
        val result = rawInputData.parseInput()
            .sumOf { scratchCard ->
                val matches = ((scratchCard.winningNumbers).intersect(scratchCard.myNumbers)).size

                return@sumOf if (matches > 0) 1 shl (matches - 1) else 0
            }

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        // This is the ugliest solution I could came up with. It works but that's it.
        // all mutability in Kotlin is not nice. Also, the approach is just stupid
        // doing step-by-step counting. This should be solved way smarter imho

        val foo = (1..rawInputData.size).associateWith { 1 } as MutableMap<Int, Int>

        rawInputData.parseInput().forEach {
            it.getWonCopies().forEach { cardNumber ->
                foo[cardNumber] = foo[cardNumber]!! + foo[it.cardNumber]!!
            }
        }

        val result = foo.values.sum()

        return "Solution: $result"
    }

    private fun List<String>.parseInput(): List<ScratchCard> {
        fun String.convert(): Set<Int> = this.split(' ')
            .filter { it.isNotBlank() }
            .map { it.toInt() }
            .toSet()

        val cardNumberRegex = "Card\\s+(\\d+):".toRegex()

        return this.map {
            val cardContent = it.substringAfter(':')

            ScratchCard(
                cardNumber = cardNumberRegex.find(it)?.groupValues?.get(1)?.toInt()
                    ?: throw RuntimeException(),
                winningNumbers = cardContent.substringBefore('|').convert(),
                myNumbers = cardContent.substringAfter('|').convert(),
            )
        }
    }

    private fun ScratchCard.getWonCopies(): List<Int> =
        winningNumbers.intersect(myNumbers).size.let { countOfMatches ->
            if (countOfMatches > 0) {
                (1..countOfMatches).map { it + cardNumber }
                    .toList()
                    .filter { it <=  rawInputData.size }
            } else emptyList()
        }

    data class ScratchCard(
        val cardNumber: Int,
        val winningNumbers: Set<Int>,
        val myNumbers: Set<Int>,
    )
}