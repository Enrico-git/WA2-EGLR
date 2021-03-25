class Node<T>(value: T){
    var value:T = value
    var next: Node<T>? = null
    var previous:Node<T>? = null
}

class Deque<T> {
    private var head: Node<T>? = null

    private fun getLastNode(): Node<T>? {
        var node = head
        if (node != null) {
            while (node?.next != null) {
                node = node.next
            }
            return node
        } else {
            return null
        }
    }

    fun push(value: T) {
        var newNode = Node(value)
        var lastNode = this.getLastNode()
        if (lastNode != null) {
            newNode.previous = lastNode
            lastNode.next = newNode
        } else {
            this.head = newNode
        }
    }

    fun pop(): T? {
        var node = this.getLastNode()
        if (node != null) {
            if(node==this.head)
            {
                this.head=null
                return node.value
            }
            var prevNode = node.previous
            prevNode?.next = null
            return node.value
        }
        return null
    }

    fun unshift(value: T) {
        var firstNode = this.head
        var newFirst = Node(value)
        firstNode?.previous = newFirst
        this.head = newFirst
        this.head?.next = firstNode
    }

    fun shift(): T? {
        var node = this.head
        if (node != null) {
            var newFirst = this.head?.next
            this.head = newFirst
            return node.value
        }
        return null
    }

}

fun main() {
    var ll = Deque<Int>()
    ll.push(1)
    ll.push(2)
    ll.push(3)
    ll.push(4)
    var last = ll.pop()
    println(last)
    ll.unshift(10)
    ll.unshift(0)
    var first = ll.shift()
    println(first)
}
