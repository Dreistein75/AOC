import Day7.HandData.Companion.CompareCardsByHighCard
import Day7.HandData.Companion.CompareCardsByType
import Day7.HandType.*

class Day7 : AoC("day7") {
    override fun getFirstSolution(): String = solveToday(joker = false)

    override fun getSecondSolution(): String = solveToday(joker = true)

    private fun solveToday(joker: Boolean): String {
        val result = rawInputData.parseInput()
            .sortedWith(CompareCardsByType(joker).then(CompareCardsByHighCard(joker)))
            .mapIndexed { index: Int, handData: HandData -> (index + 1) * handData.bid }
            .sum()

        return "Solution: $result"
    }

    private fun List<String>.parseInput(): List<HandData> = this.map {
        HandData(
            cards = it.substringBefore(" "),
            bid = it.substringAfter(" ").toInt()
        )
    }

    data class HandData(
        val cards: String,
        val bid: Int,
    ) {
        private fun getHandType(joker: Boolean): HandType {
            val groupedCards = cards.groupBy { it }

            val groupOfCards = if ( !joker ) {
                groupedCards.values
            } else {
                // Get the card != 'J' which appears most often on the hand and replace
                // all J with that (if hand == JJJJJ --> make it AAAAA)

                val cardWithMostPresence = groupedCards.filterKeys { it != 'J' }
                    .values
                    .maxByOrNull { it.size }
                    ?.first() ?: 'A'

                val handWithJokerReplaced = cards.replace(oldChar = 'J', newChar = cardWithMostPresence)

                handWithJokerReplaced.groupBy { it }.values
            }

            return groupOfCards.evaluateType()
        }

        private fun Collection<List<Char>>.evaluateType(): HandType = when {
            this.size == 1 -> FiveOfAKind
            this.any { it.size == 4 } -> FourOfAKind
            this.map { it.size }.toSet() == setOf(2,3) -> FullHouse
            this.any { it.size == 3 } -> ThreeOfAKind
            this.count { it.size == 2 } == 2 -> TwoPair
            this.any { it.size == 2 } -> OnePair
            else -> HighCard
        }

        companion object {
            fun CompareCardsByType(joker: Boolean): Comparator<HandData> =
                compareBy{ it.getHandType(joker).prio }

            fun CompareCardsByHighCard(joker: Boolean): Comparator<HandData> =
                compareBy<HandData> { it.cards.first().getValueOfCard(joker) }
                    .thenBy { it.cards[1].getValueOfCard(joker) }
                    .thenBy { it.cards[2].getValueOfCard(joker) }
                    .thenBy { it.cards[3].getValueOfCard(joker) }
                    .thenBy { it.cards.last().getValueOfCard(joker) }

            private val ALL_POSSIBLE_CARDS = "23456789TJQKA"
            private fun Char.getValueOfCard(joker: Boolean): Int =
                if ( joker && this == 'J' ) -1 else ALL_POSSIBLE_CARDS.indexOf(this)
        }
    }

    enum class HandType(val prio: Int) {
        HighCard(prio = 1),
        OnePair(prio = 2),
        TwoPair(prio = 3),
        ThreeOfAKind(prio = 4),
        FullHouse(prio = 5),
        FourOfAKind(prio = 6),
        FiveOfAKind(prio = 7),
    }
}