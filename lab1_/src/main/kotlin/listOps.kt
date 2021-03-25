val <T> List<T>.customSize: Int
        get(): Int {
            var counter = 0
            try {
                while(true) {
                    this[counter]
                    counter++
                }
            } catch (e: java.lang.IndexOutOfBoundsException) {
                return counter
            }
        }

fun <T> List<T>.customAppend(list: List<T>): List<T> {
    var newList = emptyList<T>()
    for(i in 0 until this.customSize) {
        newList = newList + this[i]
    }
    for(i in 0 until list.customSize) {
        newList = newList + list[i]
    }
    return newList
}

fun List<List<Any>>.customConcat(): List<Any> {
    var newList = emptyList<Any>()
    for(i in 0 until this.customSize) {
        newList = newList.customAppend(this[i])
    }
    return newList
}

fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
    var newList = emptyList<T>()
    for(i in 0 until this.customSize) {
        if(predicate(this[i]))
            newList = newList + this[i]
    }
    return newList
}

fun <T, U> List<T>.customMap(transform: (T) -> U): List<U> {
    var newList = emptyList<U>()
    for(i in 0 until this.customSize) {
        newList = newList + transform(this[i])
    }
    return newList
}

fun <T, U> List<T>.customFoldLeft(initial: U, f: (U, T) -> U): U {
    var res = initial
    for(i in 0 until this.customSize) {
        res = f(res,this[i])
    }
    return res
}

fun <T, U> List<T>.customFoldRight(initial: U, f: (T, U) -> U): U {
    var res = initial
    for(i in this.customSize-1 downTo 0) {
        res = f(this[i],res)
    }
    return res
}

fun <T> List<T>.customReverse(): List<T> {
    var newList = emptyList<T>()
    for(i in this.customSize-1 downTo 0) {
        newList = newList + this[i]
    }
    return newList
}

fun main() {
    var pd = listOf<Int>(1,2,3,4,5)
    println(pd.customSize)
    pd = listOf<Int>(1,2,3,4,5)
    println(pd.customAppend(listOf<Int>(6,7,8,9)))
    val pd2 = listOf<List<Any>>(listOf<Int>(1,2,3),listOf<Char>('A','B'),listOf<Int>(4,5,6,7))
    println(pd2.customConcat())
    pd = listOf<Int>(1,2,3,4,5)
    println(pd.customFilter{it%2==0})
    pd = listOf<Int>(1,2,3,4,5)
    println(pd.customMap({it*it}))
    pd = listOf<Int>(1,2,3,4,5)
    println(pd.customFoldLeft(0, {acc, item -> acc+item}))
    pd = listOf<Int>(1,2,3,4,5)
    println(pd.customFoldRight(10, {acc, item -> acc+item}))
    pd = listOf<Int>(1,2,3,4,5)
    println(pd.customReverse())
}
