data class MatrixCoordinate(val row: Int, val col: Int)

data class Matrix (val matrix: List<List<Int>>) {
    val saddlePoints: Set<MatrixCoordinate> = saddle()


    private fun saddle(): Set<MatrixCoordinate> {
        val results = mutableSetOf<MatrixCoordinate>()
        val max_row = mutableListOf<Int?>()
        val min_col = mutableListOf<Int>()

        for(i in matrix[0].indices)
            min_col.add(Int.MAX_VALUE)

        matrix.forEach{ row ->
            row.forEachIndexed { index, col ->
                if (col < min_col[index])
                    min_col[index] = col
            }
            max_row.add(row.maxByOrNull { it })
        }

        matrix.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, elem ->
                if(elem == max_row[i] && elem == min_col[j])
                    results.add(MatrixCoordinate(i, j))
            }
        }

        return results
    }
}

fun main(){
    // println(setOf<Int>(1, 2, 3, 4, 5, 1, 1, 2)) // 1, 2, 3, 4, 5
    val r1 = listOf(9, 2, 7, 6)
    val r2 = listOf(5, 3, 2, 4)
    val r3 = listOf(6, 7, 2, 3)
    //val r4 = listOf(4, 5, 6)
    val m = listOf(r1, r2, r3)

    m.forEach{println(it)}

    println(Matrix(m).saddlePoints)


}