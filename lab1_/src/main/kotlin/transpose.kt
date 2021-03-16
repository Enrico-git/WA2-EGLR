object Transpose {
    fun transpose(input: List<String>):List<String> {
        val max = input.maxByOrNull { it.length }?.length?:0
        val results = MutableList(max) {""}
        input.forEachIndexed { index, row ->
            if(row.length<max) {
                val tmp = row.padEnd(max,' ')
                tmp.forEachIndexed { col_index, c ->
                    results[col_index] += c.toString() }
            } else {
                row.forEachIndexed { col_index, c ->
                    results[col_index] += c.toString() }
            }
        }
        results.forEach { println(it) }
        return results
    }
}

fun main() {
    val tmp = listOf("AB", "DEF")
    Transpose.transpose(tmp)
}