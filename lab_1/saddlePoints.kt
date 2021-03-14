data class MatrixCoordinate(val row: Int, val col: Int)
data class Matrix (val matrix: List<List<Int>>) {
    val saddlePoints: MutableSet<MatrixCoordinate> = mutableSetOf()
    fun calculateSaddlePoints(): Unit{
//        [[8,9,1], [3,3,7]]
//        [[8,3], [9,3], [1,7]]
        val potentialPoints = mutableListOf<MatrixCoordinate>()
        val transposedMatrix = mutableListOf<MutableList<Int>>()
        for (i in 0..matrix.get(0).size)
            transposedMatrix.add(mutableListOf<Int>())
        matrix.forEachIndexed{index, row ->
            row.forEachIndexed{index, point -> transposedMatrix[index].add(point)}
            try {
                val max = row.maxOrNull()
                if (max != null)
                    row.forEachIndexed{ index2, point ->
                        if (point == max)
                            potentialPoints.add(MatrixCoordinate(index, index2))
                    }
                else
                    throw Exception("Null Row")
            } catch(e: Exception){
                println(e)
            }
        }
        println(potentialPoints)
        potentialPoints.forEach{point ->
            val min = transposedMatrix[point.col].minOrNull()
            transposedMatrix[point.col].forEachIndexed{index, point2 ->
                if (min == point2 && point.row == index)
                    saddlePoints.add(point)
            }
        }

    }
}

fun main(){
    val matrix = listOf(listOf(9,8,7), listOf(5,3,2), listOf(6,6,7))
    val setUp = Matrix(matrix)
    setUp.calculateSaddlePoints()
    println(setUp.saddlePoints)
}