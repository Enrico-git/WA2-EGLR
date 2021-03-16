object Yacht {
    fun solve(category: YachtCategory, d1: Int, d2: Int, d3: Int, d4: Int, d5: Int): Int {
        val list = arrayListOf<Int>(d1, d2, d3, d4, d5)
        println(list)
        /*var points:Int = 0
        when(category){
            YachtCategory.ONES -> {
              points = mult(list, 1)
            }

        }

        return points*/
        return when (category) {
            YachtCategory.ONES -> mult(list, 1)
            YachtCategory.TWOS -> mult(list, 2)
            YachtCategory.THREES -> mult(list, 3)
            YachtCategory.FOURS -> mult(list, 4)
            YachtCategory.FIVES -> mult(list, 5)
            YachtCategory.SIXES -> mult(list, 6)
            YachtCategory.FULL_HOUSE -> {
                val map = list.groupingBy { it }.eachCount()
                if (map.size == 2)
                    if (map.containsValue(3))
                        return list.sum()
                return 0
            }
            YachtCategory.FOUR_OF_A_KIND -> {
                val map = list.groupingBy { it }.eachCount()
                if (map.size == 2) {
                    if (map.containsValue(4))
                        return map.keys.first { map[it] == 4 } * 4
                }
                return 0
            }
            YachtCategory.LITTLE_STRAIGHT -> {
                /*list.sorted().containsAll(listOf(1, 2, 3, 4, 5, 6))
                if(list.sorted() == listOf(1, 2, 3, 4, 5))
                    return 30
                return 0*/
                return list.isStraight(listOf(1, 2, 3, 4, 5))
            }
            YachtCategory.BIG_STRAIGHT -> {
                return list.isStraight(listOf(2, 3, 4, 5, 6))
                /*if(list.containsAll(listOf(2, 3, 4, 5, 6)))
                    return 30
                return 0*/
            }
            YachtCategory.CHOICE -> {
                return list.sum()
            }
            YachtCategory.YACHT -> {
                val map = list.groupingBy { it }.eachCount()
                if(map.size == 1)
                    return 50
                else
                    0
            }

            else -> 0
        }

    }

    fun ArrayList<Int>.isStraight(list: List<Int>) =
        if (this.containsAll(list)) 30 else  0


    private fun mult(list: ArrayList<Int>, mul: Int): Int = list.count { it == mul } * mul
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


fun main (){
    println(Yacht.solve(YachtCategory.YACHT, 1, 1, 1, 1, 1))

}