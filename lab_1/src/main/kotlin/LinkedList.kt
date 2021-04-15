class Deque<T> {

    val list = mutableListOf<T>()

    fun push(value: T) {
        list.add(value)
    }

    fun pop(): T? {
        return list.removeLastOrNull()
    }

    fun unshift(value: T) {
        list.add(0, value)
    }

    fun shift(): T? {
        return list.removeFirstOrNull()
    }
}

fun main(){
    val tmp = Deque<Int>()
    println(tmp.pop())      //null
    println(tmp.shift())    //null
    tmp.push(12)        //12
    tmp.unshift(3)      //3, 12
    tmp.unshift(5)      //5, 3, 12
    println(tmp.pop())       //5, 3
    println(tmp.shift())     //3
}