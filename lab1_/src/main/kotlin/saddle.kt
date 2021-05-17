data class MatrixCoordinate(val row: Int, val col: Int)

data class Matrix(val matrix: List<List<Int>>) {
    val saddlePoints: Set<MatrixCoordinate> = findSaddlePoints()
    private fun findSaddlePoints(): Set<MatrixCoordinate> {
        var list = mutableSetOf<MatrixCoordinate>()
        var mins = mutableListOf<Int>()
        var maxs = mutableListOf<Int>()
        var i = 0
        while (i < matrix[0].size) {
            mins.add(Int.MAX_VALUE)
            i++
        }
        matrix.forEachIndexed { index, r ->
            r.forEachIndexed { index1, c ->
                if (c < mins[index1])
                    mins[index1] = c
            }
            maxs.add(r.maxOrNull() ?: 0)
        }
        mins.forEachIndexed { index, col ->
            if (maxs.contains(col)) {
                var index2 = maxs.indexOf(col)
                var point = MatrixCoordinate(index2, index)
                list.add(point)
            }
        }
        return list
    }
}

fun main() {
    var riga1 = listOf<Int>(9, 8, 7)
    var riga2 = listOf<Int>(5, 3, 2)
    var riga3 = listOf<Int>(6, 6, 7)
    var matrice = listOf(riga1, riga2, riga3)
    var mat = Matrix(matrice)
    println(mat.saddlePoints)
}
