object Transpose {
    fun transpose(input: List<String>):List<String> {
        val max = input.maxByOrNull { it.length }?.length?:0
        val results = MutableList(max) {""}
        println(results)
        for(e in 0..max)
            results.add("")
        //println(results)
        input.forEachIndexed{index, row ->
            var tmp = row
            if(row.length < max){
                val tmp = row.padEnd(max)
                println(max)
                //println(tmp.length)

            }
            tmp.forEachIndexed {col_index, c ->
                results[col_index] += c.toString() }
        }

        results.forEach{println(it)}
        return results
    }

}

fun main(){
    val tmp = listOf("AB", "DEF")
    Transpose.transpose(tmp)

}