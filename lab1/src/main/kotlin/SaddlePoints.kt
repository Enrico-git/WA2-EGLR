data class MatrixCoordinate(val row: Int, val col: Int)

data class Matrix (val matrix: List<List<Int>>) {
    val saddlePoints: Set<MatrixCoordinate> = findSaddle()
    //TODO: implement me

   fun findSaddle(): Set<MatrixCoordinate>{
        var results = mutableSetOf<MatrixCoordinate>()

        matrix.forEachIndexed { index1, row ->
            row.forEachIndexed{ index2, element ->
                if(element == row.maxOrNull()?:0){
                    var tmp = mutableListOf<Int>()
                    matrix.forEach{list1 ->
                        tmp.add(list1[index2])
                    }
                    if(element == tmp.minOrNull()?:0) {
                        var point = MatrixCoordinate(index1, index2)
                        results.add(point)
                    }
                }
            }
        }
        return results

    }
}

fun main(){
    val r1 = listOf(9, 8, 7)
    val r2 = listOf(5, 3, 2)
    val r3 = listOf(6, 6, 7)

    val matrix = listOf(r1, r2, r3)

    println(matrix)
    println(Matrix(matrix).findSaddle())

}