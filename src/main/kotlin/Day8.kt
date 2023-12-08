class Day8 : AoC("day8") {
    override fun getFirstSolution(): String {
        val result = "AAA".getPathLengthToTarget(
            directions = rawInputData.extractDirections(),
            desertMap = rawInputData.extractMaps(),
            isTargetNode = { this == "ZZZ" }
        )

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val mapData = rawInputData.extractMaps()
        val startNodes = mapData.keys.filter { it.endsWith('A') }

        val cycleLengths = startNodes.map {
            it.getPathLengthToTarget(
                directions = rawInputData.extractDirections(),
                desertMap = mapData,
                isTargetNode = { this.endsWith('Z') }
            )
        }.map { it.toLong() }


        return "Solution: ${cycleLengths.getLCM()}"
    }

    private fun List<String>.extractMaps(): Map<String, Pair<String, String>> {
        return this.subList(fromIndex = 2, toIndex = this.size)
            .map { it.buildNode() }
            .associate { it.first to (it.second to it.third) }
    }

    private fun String.buildNode(): Triple<String, String, String> {
        val pattern = "([A-Z]{3})\\s*=\\s*\\(([A-Z]{3}),\\s*([A-Z]{3})\\)".toRegex()
        val matches = pattern.find(this)

        return with(matches!!) {
            Triple(groupValues[1], groupValues[2], groupValues[3])
        }
    }

    private fun List<String>.extractDirections(): List<Char> = this.first().toList()

    private fun String.getVisitedLocations(
        directions: List<Char>,
        desertMap: Map<String, Pair<String, String>>
    ): List<String> = directions.runningFold(this) { currPos, direction ->
        desertMap[currPos]!!.let { if (direction == 'L') it.first else it.second }
    }


    private tailrec fun String.getPathLengthToTarget(
        directions: List<Char>,
        desertMap: Map<String, Pair<String, String>>,
        isTargetNode: String.() -> Boolean,
        offset: Int = 0
    ): Int {
        val path = this.getVisitedLocations(directions, desertMap)
        val indexOfFirstTarget = path.indexOfFirst { it.isTargetNode() }

        return if (indexOfFirstTarget != -1) offset + indexOfFirstTarget else {
            path.last().getPathLengthToTarget(
                directions = directions,
                desertMap = desertMap,
                isTargetNode = isTargetNode,
                offset = offset + directions.size
            )
        }
    }

    private fun List<Long>.getLCM(): Long {
        tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
        fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

        return this.reduce { acc, num -> lcm(acc, num) }
    }
}