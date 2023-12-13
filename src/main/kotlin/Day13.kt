class Day13 : AoC("day13") {
    override fun getFirstSolution(): String {
        val result = rawInputData.extractClusters()
            .flatMap { it.collectSymmetries() }
            .sum()

        return "Solution: $result"
    }

    override fun getSecondSolution(): String {
        val allClusters = rawInputData.extractClusters()

        val allSymmetries = allClusters.flatMap { it.removeSmudge()
            .collectSymmetries()
            .minus(it.collectSymmetries().toSet())
        }

        return "Solution: ${allSymmetries.sum()}"
    }

    private fun Cluster.collectSymmetries(): List<Int> = this.findColumnSymmetry() +
        this.findRowSymmetry()

    private fun List<String>.extractClusters(): List<Cluster> {
        return this.fold(listOf(emptyList<String>())) { acc, line ->
            if (line.isEmpty()) {
                acc + listOf(emptyList())
            } else {
                acc.dropLast(1) + listOf(acc.last() + line)
            }
        }
            .filter { it.isNotEmpty() }
            .map { Cluster(it) }
    }

    data class Cluster(
        val rows: List<String>
    ) {
        fun findColumnSymmetry(): List<Int> = this.transpose().getRowSymmetryIndices()

        fun findRowSymmetry(): List<Int> = getRowSymmetryIndices().map { it * 100 }

        private fun getRowSymmetryIndices(): List<Int> {
            val symmetryIndex = (1..< rows.size).filter {
                rows.subList(0, it).isSymmetricTo(rows.subList(it, rows.size))
            }

            return symmetryIndex
        }

        private fun flipEntry(rowIndex: Int, colIndex: Int) = Cluster(
            rows.mapIndexed { index, row ->
                if (index == rowIndex) row.flipEntry(colIndex) else row
           }
        )

        fun removeSmudge(): Cluster {
            val currentSymmetries = (findColumnSymmetry() + findRowSymmetry()).toSet()

            return getCoordinates().map { (row, col) -> this.flipEntry(row, col) }
                .first { cluster ->
                    val symmetries = cluster.findRowSymmetry() + cluster.findColumnSymmetry()

                    return@first symmetries.minus(currentSymmetries).size == 1
                }
        }

        private fun getCoordinates(): List<Pair<Int, Int>> {
            val nbrOfCols = rows.first().length

            return rows.indices.flatMap { rowIndex ->
                (0 until nbrOfCols).map { colIndex -> rowIndex to colIndex }
            }
        }

        private fun transpose() = Cluster(
            rows = rows.first().toList().indices.map { colIndex ->
                rows.map { it[colIndex] }.joinToString(separator = "")
            }
        )

        private fun String.flipEntry(index: Int): String = this.mapIndexed { idx, char ->
            if (index != idx) char else char.flip()
        }.joinToString(separator = "")

        private fun Char.flip(): Char =
            if (this == '#') '.' else '#'

        private fun List<String>.isSymmetricTo(other: List<String>): Boolean {
            return other.indices.all { idx ->
                if (idx >= this.size) true else other[idx] == this[this.lastIndex - idx]
            }
        }
    }
}