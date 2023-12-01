import Day1.Exercise.ExerciseOne
import Day1.Exercise.ExerciseTwo

class Day1 : AoC("day1") {
    override fun getFirstSolution(): String {
        val result = inputData.calculateSolution {
            this.getCalibrationValueForCurrentLine(ExerciseOne)
        }

        return "Solution is: $result"
    }

    override fun getSecondSolution(): String {
        val result = inputData.calculateSolution {
            this.getCalibrationValueForCurrentLine(ExerciseTwo)
        }

        return "Solution is: $result"
    }

    private fun List<String>.calculateSolution(evaluateThisLine: String.() -> Int) =
        this.fold(initial = 0) {
                acc, inputLine -> acc + inputLine.evaluateThisLine()
        }

    private fun String.getCalibrationValueForCurrentLine(exercise: Exercise): Int {
        val (firstDigit, lastDigit) = when(exercise) {
            ExerciseOne -> this.extractDigitsInFirstExercise()
            ExerciseTwo -> this.extractDigitsInSecondExercise()
        }

        return 10 * firstDigit + lastDigit
    }

    private fun String.extractDigitsInFirstExercise(): Pair<Int, Int> {
        val firstDigit = this.first { it.isDigit() }.digitToInt()
        val lastDigit = this.last { it.isDigit() }.digitToInt()

        return firstDigit to lastDigit
    }

    private fun String.extractDigitsInSecondExercise(): Pair<Int, Int> {
        val firstDigit = this.findFirstWithRegex(wordsToSearchFor = DIGIT_MAP.keys.toList())
        val lastDigit = this.reversed().findFirstWithRegex(DIGIT_MAP.keys.map { it.reversed() }).reversed()

        return firstDigit.convertToInt() to lastDigit.convertToInt()
    }

    private fun String.findFirstWithRegex(wordsToSearchFor: List<String>): String {
        val wordsToSearchForJoined = wordsToSearchFor.joinToString("|")
        val regexPattern = """\d|$wordsToSearchForJoined""".toRegex()
        val allResults = regexPattern.findAll(this).map { it.value }

        return allResults.first()
    }

    private fun String.convertToInt(): Int {
        return if (this.length == 1 && this.first().isDigit()) {
            this.first().digitToInt()
        } else {
            DIGIT_MAP[this] ?: throw RuntimeException()
        }
    }

    enum class Exercise {
        ExerciseOne,
        ExerciseTwo,
    }

    companion object {
        private val DIGIT_MAP = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )
    }
}