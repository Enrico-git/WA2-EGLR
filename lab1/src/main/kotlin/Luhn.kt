object Luhn {

    fun isValid (candidate:String): Boolean {
        val input = candidate.filter { it != ' ' }.reversed()
        if(input.length <= 1 || input.any { !it.isDigit() })
            return false

        var sum = 0
        input.forEachIndexed{ind, c ->
            var i: Int = c.toString().toInt()
            if(ind%2 != 0){
                i *= 2
                if(i > 9)
                    i -= 9
            }
            sum += i
        }

        println(sum)
        if(sum%10 == 0)
            return true

        return false

    }
}

fun main(){
    println(Luhn.isValid("4539 1488 0343 6467"))
    println(Luhn.isValid("123 456"))
    println(Luhn.isValid("!"))

}