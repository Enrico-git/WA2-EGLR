object Yacht {
    fun solve(category: YachtCategory, d1: Int, d2: Int, d3: Int, d4: Int,
              d5: Int): Int {
        val list = listOf(d1,d2,d3,d4,d5)
        when(category){
            YachtCategory.YACHT -> {
                if (list.distinct().size == 1)
                    return 50
                else
                    return 0
            }
            YachtCategory.ONES -> {
                return list.filter{it == 1}.size
            }
            YachtCategory.TWOS -> {
                return list.filter{it == 2}.size * 2
            }
            YachtCategory.THREES -> {
                return list.filter{it == 3}.size * 3
            }
            YachtCategory.FOURS -> {
                return list.filter{it == 4}.size * 4
            }
            YachtCategory.FIVES -> {
                return list.filter{it == 5}.size * 5
            }
            YachtCategory.SIXES -> {
                return list.filter{it == 6}.size * 6
            }
            YachtCategory.FULL_HOUSE -> {

                if (list.distinct().size == 2 ) {
                    val grouped = list.groupBy { it }
                    val condition = grouped.values.toList().get(0).size == 2 || grouped.values.toList().get(0).size == 3
                    if (condition)
                        return list.sum()
                    else
                        return 0
                }
                else
                    return 0
            }
            YachtCategory.FOUR_OF_A_KIND -> {

                if (list.distinct().size == 2){
                    val grouped = list.groupBy { it }
                    val condition = grouped.values.toList().get(0).size == 1 || grouped.values.toList().get(0).size == 4
                    if (condition)
                        return grouped.values.toList().filter{it.size == 4}.get(0).sum()
                    else
                        return 0
                }

                else
                    return 0
            }
            YachtCategory.LITTLE_STRAIGHT -> {
                if (list.sorted() == listOf<Int>(1,2,3,4,5))
                    return 30
                else
                    0
            }
            YachtCategory.BIG_STRAIGHT -> {
                if (list.sorted() == listOf<Int>(2,3,4,5,6))
                    return 30
                else
                    0
            }
            YachtCategory.CHOICE -> {
                return list.sum()
            }
        }
        return 0
    }
    enum class YachtCategory {
        YACHT,
        ONES,TWOS,
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

fun main(){

    println(Yacht.solve(Yacht.YachtCategory.YACHT, 1,1,1,1,1))
    println(Yacht.solve(Yacht.YachtCategory.ONES, 1,1,1,1,2))
    println(Yacht.solve(Yacht.YachtCategory.TWOS, 1,1,1,2,2))
    println(Yacht.solve(Yacht.YachtCategory.THREES, 3,1,3,1,1))
    println(Yacht.solve(Yacht.YachtCategory.FOURS, 1,4,5,5,4))
    println(Yacht.solve(Yacht.YachtCategory.FIVES, 6,5,1,5,1))
    println(Yacht.solve(Yacht.YachtCategory.SIXES, 1,1,1,6,1))
    println(Yacht.solve(Yacht.YachtCategory.FULL_HOUSE, 1,1,1,2,2))
    println(Yacht.solve(Yacht.YachtCategory.FOUR_OF_A_KIND, 1,1,1,1,2))
    println(Yacht.solve(Yacht.YachtCategory.LITTLE_STRAIGHT, 1,2,3,5,4))
    println(Yacht.solve(Yacht.YachtCategory.BIG_STRAIGHT, 6,2,3,5,4))
    println(Yacht.solve(Yacht.YachtCategory.CHOICE, 6,2,3,5,4))
}