object Transpose {
    fun transpose(input: List<String>): List<String> {
        val max = input.maxByOrNull { it.length }?.length?:0
        val results = MutableList<String> (max) {""}

        input.forEachIndexed { index, row ->
            var tmp = row
            if(row.length < max){
                tmp = row.padEnd(max)
            }

            tmp.forEachIndexed { col_index, c ->
                results[col_index] += c.toString() }
        }

        results.forEach{    println(it)        }
        return results
    }
}


fun main(){
    val tmp = listOf("ABC", "EFG")
    Transpose.transpose(tmp)
}