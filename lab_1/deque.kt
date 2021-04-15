package deque

class Node<T>(val value: T, var prev: Node<T>?, var next:Node<T>?){
    override fun toString(): String {
        return "[$value, ${prev?.value}, ${next?.value}]"
    }
}

class Deque<T> {
    var deque:MutableList<Node<T>> = mutableListOf<Node<T>>()

    fun push(value: T) {
        if (deque.size != 0){
            val prev = deque.get(deque.size-1)
            val item = Node<T>(value, prev, null)
            deque.last().next = item
            deque.add(item)
        } else
            deque.add(Node<T>(value, null, null))
    }
    fun pop(): T? {
        if (deque.size > 0) {
            val value = deque.removeLast().value
            deque.last().next = null
            return value
        }
        else
            return null
    }
    fun unshift(value: T) {
        if (deque.size != 0){
            val item = Node<T>(value, null, deque.get(0))
            deque.get(0).prev = item
            val list = mutableListOf<Node<T>>(item)
            list.addAll(1, deque)
            deque = list
        } else
            deque.add(Node<T>(value, null, null))
    }
    fun shift(): T? {
        if (deque.size > 0) {
            val value = deque.removeFirst().value
            deque[0].prev = null
            return value
        }
        else
            return null
    }

    override fun toString(): String {
        return deque.toString()
    }
}

fun main(){
    val deque = Deque<Int>()
    deque.push(2)
    deque.push(4)
    deque.push(6)
    println(deque)
    println(deque.pop())
    println(deque)
    deque.unshift(13)
    println(deque)
    println(deque.shift())
    println(deque)
}