package kt.first.lesson

import kotlinx.coroutines.runBlocking


// 看不懂代码没关系，目前咱们只需要关心代码的执行结果
fun main() = runBlocking {
    val sequence = getSequence()
    printSequence(sequence)
}

fun getSequence() = sequence {
    log("Add 1")
    yield(1)
    log("Add 2")
    yield(2)
    log("Add 3")
    yield(3)
    log("Add 4")
    yield(4)
}

fun printSequence(sequence: Sequence<Int>) {
    val iterator = sequence.iterator()
    val i = iterator.next()
    log("Get$i")
    val j = iterator.next()
    log("Get$j")
    val k = iterator.next()
    log("Get$k")
    val m = iterator.next()
    log("Get$m")
}

/*
输出结果：
Add 1
Get1
Add 2
Get2
Add 3
Get3
Add 4
Get4
*/