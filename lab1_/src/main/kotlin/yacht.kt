object Yacht {

    fun solve(category: YachtCategory, d1: Int, d2: Int, d3: Int, d4: Int, d5: Int): Int {
        val dice = listOf<Int>(d1, d2, d3, d4, d5)
        when (category) {
            YachtCategory.YACHT -> {
                val map = dice.groupingBy { it }.eachCount()
                if (map.size == 1)
                    return 50
                return 0
            }
            YachtCategory.ONES -> {
                return dice.filter { it == 1 }.size
            }
            YachtCategory.TWOS -> {
                return dice.filter { it == 2 }.size * 2
            }
            YachtCategory.THREES -> {
                return dice.filter { it == 3 }.size * 3
            }
            YachtCategory.FOURS -> {
                return dice.filter { it == 4 }.size * 4
            }
            YachtCategory.FIVES -> {
                return dice.filter { it == 5 }.size * 5
            }
            YachtCategory.SIXES -> {
                return dice.filter { it == 6 }.size * 6
            }
            YachtCategory.FULL_HOUSE -> {
                val map = dice.groupingBy { it }.eachCount()
                if (map.size == 2)
                    if (map.containsValue(3))
                        return dice.sum()
                return 0
            }
            YachtCategory.FOUR_OF_A_KIND -> {
                val map = dice.groupingBy { it }.eachCount()
                if (map.size == 2) {
                    if (map.containsValue(4))
                        return map.keys.first { map[it] == 4 } * 4
                }
                return 0
            }
            YachtCategory.LITTLE_STRAIGHT -> {
                if (dice.containsAll(listOf(1, 2, 3, 4, 5)))
                    return 30
                return 0
            }
            YachtCategory.BIG_STRAIGHT -> {
                if (dice.containsAll(listOf(2, 3, 4, 5, 6)))
                    return 30
                return 0
            }
            YachtCategory.CHOICE -> {
                return dice.sum()
            }
        }
        return 0
    }

    enum class YachtCategory {
        YACHT,
        ONES,
        TWOS,
        THREES,
        FOURS,
        FIVES,
        SIXES,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        LITTLE_STRAIGHT,
        BIG_STRAIGHT,
        CHOICE
    }
}

fun main() {
    println(Yacht.solve(Yacht.YachtCategory.ONES, 1, 2, 3, 4, 5))
}