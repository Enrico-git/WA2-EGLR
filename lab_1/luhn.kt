object Luhn {
    fun isValid(candidate: String): Boolean {
        var input = candidate.replace("\\s".toRegex(), "")
        val regex = "^[0-9]*$".toRegex()
        if (!regex.matches(input))
            return false
        if (input.length <= 1)
            return false
        val x = input.mapIndexed<Int> { index, char ->
            var num = 2 * char.toString().toInt()
            if (index % 2 == 0) {
                if (num > 9)
                    num = num - 9
            } else
                num = char.toString().toInt()
            num
        }
        return x.sum() % 10 == 0
    }
}

fun main() {
    println(Luhn.isValid("4539  1488 0343 6467"))
    println(Luhn.isValid("4539  1488 0343 6466"))
    println(Luhn.isValid("4539  1f88 0343 6467"))

}