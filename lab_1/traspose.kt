object Transpose {
    fun transpose(input: List<String>): List<String> {
        // 123 123 123 12
        // 1111 2222 333
        val output = mutableListOf<String>()
        val max = input.maxByOrNull{ it -> it.length}?.length?:0
        val input_padded = input.map{row -> if (row.length < max) row.padEnd(max) else row}
        for (i in 0..max - 1) {
            var string = ""
            for (j in 0..input_padded.size - 1) {
                string += input_padded[j][i]
            }
            output.add(string)
        }
        return output
    }
}

fun main(){
    val input = listOf<String>("ABC", "DE", "AJK")
    print(Transpose.transpose(input))
}
