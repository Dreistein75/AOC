import Day15.InstructionType.REMOVAL

class Day15 : AoC("day15") {
    override fun getFirstSolution(): String {
        val result = rawInputData.first()
            .split(',')
            .sumOf { it.hash() }

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val result = rawInputData.first().split(',')
            .map { it.parseInstruction() }
            .executeAll()
            .calcTotalFocalPower()

        return "Solution: $result"
    }

    private fun String.hash(): Int = this.toList()
        .fold(0) { acc, char ->
            ((acc + char.code) * 17) % 256
        }

    private fun String.parseInstruction(): Instruction {
        val pattern = """^([a-z]+)([-=])(\d*)$""".toRegex()
        val (label, type, focalLen) = pattern.matchEntire(this)!!.destructured

        return Instruction(
            label = label,
            type = InstructionType.parse(type),
            focalLength = focalLen.let { if(it.isNotBlank()) it.toInt() else null }
        )
    }

    private fun List<Box>.calcTotalFocalPower(): Int = this.mapIndexed { index, box ->
        (index + 1) * box.lenses.mapIndexed { lenIndex, lens ->
            (lenIndex + 1) * lens.focalLength
        }.sum()
    }.sum()

    private fun List<Instruction>.executeAll(): List<Box> {
        val initialSetup = List(256) { Box(emptyList()) }

        return this.fold(initialSetup) { acc, instruction -> acc.executeInstruction(instruction) }
    }

    private fun List<Box>.executeInstruction(instruction: Instruction): List<Box> {
        val affectedBox = instruction.label.hash()

        return this.mapIndexed { index, box ->
            if (index == affectedBox) box.executeInstruction(instruction) else box
        }
    }

    data class Box(
        val lenses: List<Lens>,
    ) {
        fun executeInstruction(instruction: Instruction): Box {
            return if(instruction.type == REMOVAL) {
                removeLens(instruction.label)
            } else {
                insertLens(Lens(instruction.label, instruction.focalLength!!))
            }
        }

        private fun removeLens(label: String) = Box(lenses.filter { it.label != label })

        private fun insertLens(newLens: Lens): Box {
            val existingLensWithThatLabel = lenses.indexOfFirst { it.label == newLens.label }

            return if (existingLensWithThatLabel == -1) {
                addLens(newLens)
            } else {
                replaceLens(existingLensWithThatLabel, newLens)
            }
        }

        private fun addLens(newLens: Lens) = Box(lenses.plus(newLens))

        private fun replaceLens(index: Int, newLens: Lens) = Box(
            lenses.mapIndexed { idx, oldLens -> if(idx == index) newLens else oldLens }
        )
    }

    data class Lens(
        val label: String,
        val focalLength: Int,
    )

    data class Instruction(
        val type: InstructionType,
        val label: String,
        val focalLength: Int? = null
    )

    enum class InstructionType {
        REMOVAL,
        INSERTION;

        companion object {
            fun parse(instruction: String): InstructionType =
                if(instruction.contains('-')) REMOVAL else INSERTION
        }
    }
}