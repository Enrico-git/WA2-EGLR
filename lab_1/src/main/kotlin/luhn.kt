object Luhn {
    fun isValid(candidate: String): Boolean {
        if( (candidate.length < 2) || (!candidate.matches("[0-9 ]+".toRegex())))
            return false

        println(candidate)

        val doubled = candidate
            .reversed()
            .split(" ")
            .map {
                var tmp = ""
                it.forEachIndexed { index, c ->
                    if(index == 1 || index == 3) {
                        val target = Character.getNumericValue(c)
                        val digit = if (2 * target > 9) (2 * target - 9) else (2 * target)
                        tmp += digit.toString()
                    }else
                        tmp += c
                }
                tmp
            }

        val result = doubled.fold("") {tot, el -> if(tot == "") tot+el else tot+" "+el}.reversed()
        println(result)

        val sum = result.fold(0) {sum, el -> if(el != ' ') sum+ Character.getNumericValue(el) else sum+0}
        println("sum: $sum")
        return sum%10 == 0
    }
}

fun main(){
    println(Luhn.isValid("4539 1488 0343 6467"))
    println(Luhn.isValid("4539 1488 0343 6468"))
}