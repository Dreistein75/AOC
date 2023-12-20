import Day18.Direction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException

class Day19 : AoC("day19") {
    override fun getFirstSolution(): String {
        val (workflows, parts) = rawInputData.parse()

        val result = parts.filter { it.isAccepted(workflows) }
            .sumOf { it.evaluate() }

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val (workflows, _) = rawInputData.parse()
        val result = runBlocking {
            val xResult = (1 .. 4000).map { x->
                async(Dispatchers.Default) {
                    (1 .. 4000).sumOf { m ->
                        (1 .. 4000).sumOf { a ->
                            (1 .. 4000).sumOf { s ->
                              if (Part(mapOf('x' to x, 'm' to m, 'a' to a, 's' to s)).isAccepted(workflows))
                                1L
                                else 0L
                            }
                        }
                    }
                }
            }.awaitAll().sum()
        }

        return "Solution: $result"
    }

    private fun List<String>.parse(): Pair<List<Workflow>, List<Part>> {
        val emptyLine = this.indexOf("")
        val workflows = this.subList(0, emptyLine)
            .map { it.parseWorkflow() }
        val parts = this.subList(emptyLine + 1, this.size)
            .map { it.parsePart() }

        return workflows to parts
    }

    private fun String.parsePart(): Part {
        val partsPattern = """x=(\d+),m=(\d+),a=(\d+),s=(\d+)""".toRegex()
        val (x, m, a, s) = partsPattern.find(this)!!.destructured
        val catMap = mapOf('x' to x.toInt(), 'm' to m.toInt(), 'a' to a.toInt(), 's' to s.toInt())

        return Part(catMap)
    }

    private fun String.parseWorkflow(): Workflow {
        val name = this.substringBefore('{')
        val content = this.substringAfter('{').dropLast(1).split(',')
        val fallbackResult = content.last()
        val rules = content.dropLast(1).map {
            val (conditionString, result) = it.split(':')
            return@map Rule(
                condition = conditionString.parseCondition(),
                result = result
            )
        }
            .plus( Rule(condition = {true}, result = fallbackResult) )

        return Workflow(name, rules)
    }

    private fun String.parseCondition(): (Part) -> Boolean {
        val cat = this.first()
        val threshold = this.substring(startIndex = 1 + this.indexOfFirst { it == '<' || it == '>' })
            .toInt()

        return when (this[1]) {
            '<' ->  { part -> part.cats[cat]!! < threshold }
            '>' -> { part -> part.cats[cat]!! > threshold }
            else -> throw RuntimeException()
        }
    }

    private fun Part.isAccepted(workflows: List<Workflow>): Boolean {
        val foo = generateSequence(workflows.get("in")) { workflow ->
            val nextRule = workflow.applyTo(this)
            workflows.get(nextRule)
        }.distinct().toList().last()

        return foo.evaluateOn(this)
    }

    private fun Workflow.applyTo(part: Part): String {
        val useRule = this.rules.firstOrNull { it.condition.invoke(part) } ?: this.rules.last()
        return useRule.result
    }

    private fun Workflow.evaluateOn(part: Part): Boolean {
        return this.rules.first().condition.invoke(part)
    }

    private fun List<Workflow>.get(name: String): Workflow? {
        return when(name) {
            "A" -> Workflow(name = null, rules = listOf(Rule({true}, "")))
            "R" -> Workflow(name = null, rules = listOf(Rule({false}, "")))
            else -> this.firstOrNull { it.name == name }
        }
    }

    data class Part(
        val cats: Map<Char, Int>
    ) {
        fun evaluate(): Long = cats.values.sum().toLong()
    }

    data class Workflow(
        val name: String?,
        val rules: List<Rule>
    )

    data class Rule(
        val condition: (Part) -> Boolean,
        val result: String
    )
}