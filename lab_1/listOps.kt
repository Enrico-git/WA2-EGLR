import java.util.*

val <T> List<T>.customSize: Int
    get():Int {
        var counter = 0
        try {
            while (true) {
                this[counter]
                counter++
            }
        } catch (e: java.lang.IndexOutOfBoundsException) {
            return counter
        }
    }

fun <T> List<T>.customAppend(list: List<T>): List<T> {
    val size = this.customSize
    val listSize = list.customSize
    var out = emptyList<T>()
    for (i in 0..size - 1)
        out = out + this[i]
    for (i in 0..listSize - 1)
        out = out + list[i]
    return out
}

fun List<List<Any>>.customConcat(): List<Any> {
    val size = this.customSize
    var out = emptyList<Any>()
    for (i in 0..size - 1)
        for (j in 0..this[i].size - 1)
            out = out + this[i][j]
    return out
}

fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
    val size = this.customSize
    var out = emptyList<T>()
    for (i in 0..size - 1)
        if (predicate(this[i]))
            out = out + this[i]
    return out
}

fun <T, U> List<T>.customMap(transform: (T) -> U): List<U> {
    val size = this.customSize
    var out = emptyList<U>()
    for (i in 0..size - 1)
        out = out + transform(this[i])
    return out
}

fun <T, U> List<T>.customFoldLeft(initial: U, f: (U, T) -> U): U {
    val size = this.customSize
    var acc = initial
    for (i in 0..size - 1)
        acc = f(acc, this[i])
    return acc

}

fun <T, U> List<T>.customFoldRight(initial: U, f: (T, U) -> U): U {
    val size = this.customSize
    var acc = initial
    for (i in size - 1 downTo 0)
        acc = f(this[i], acc)
    return acc
}

fun <T> List<T>.customReverse(): List<T> {
    val size = this.customSize
    var out = emptyList<T>()
    for (i in size - 1 downTo 0)
        out = out + this[i]
    return out
}

fun main() {
    var pd = listOf<Int>(1, 2, 3)
    println(pd.customSize)
    pd = listOf<Int>(1, 2, 3)
    println(pd.customAppend(listOf<Int>(4, 5, 6)))
    val pd2 = listOf<List<Int>>(listOf<Int>(1, 2), listOf<Int>(1, 2, 4, 5), listOf<Int>(10, 11))
    println(pd2.customConcat())
    pd = listOf<Int>(1, 2, 3, 4, 5)
    println(pd.customFilter { it % 2 == 0 })
    pd = listOf<Int>(1, 2, 3, 4, 5)
    println(pd.customMap({ it * 2 }))
    pd = listOf<Int>(1, 2, 3, 4, 5)
    println(pd.customFoldLeft(20, { acc, item -> acc + item }))
    pd = listOf<Int>(1, 2, 3, 4, 5)
    println(pd.customFoldRight(29, { acc, item -> acc + item }))
    pd = listOf<Int>(1, 2, 3, 4, 5)
    println(pd.customReverse())

}