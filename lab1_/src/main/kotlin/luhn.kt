object Luhn {
    fun isValid(candidate: String): Boolean {
        var inverse = candidate.filter { it != ' ' }.reversed()
        if (inverse.length <= 1 || inverse.any { !it.isDigit() })
            return false
        var sum = 0
        inverse.forEachIndexed { index, c ->
            var i: Int = c.toString().toInt()
            if (index % 2 != 0) {
                i *= 2
                if (i > 9)
                    i -= 9
            }
            sum += i
        }
        println(sum)
        if (sum % 10 == 0)
            return true
        return false
    }
}

fun main() {
    println(Luhn.isValid("4539 1488 0343 6467"))
}
