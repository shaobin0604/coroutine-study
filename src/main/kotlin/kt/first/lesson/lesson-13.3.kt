package kt.first.lesson

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


// 看不懂代码没关系，目前咱们只需要关心代码的执行结果
fun main() = runBlocking {
    val channel = getProducer(this)
    testConsumer(channel)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun getProducer(scope: CoroutineScope) = scope.produce {
    log("Send:1")
    send(1)
    log("Send:2")
    send(2)
    log("Send:3")
    send(3)
    log("Send:4")
    send(4)
}

suspend fun testConsumer(channel: ReceiveChannel<Int>) {
    delay(100)
    val i = channel.receive()
    log("Receive$i")
    delay(100)
    val j = channel.receive()
    log("Receive$j")
    delay(100)
    val k = channel.receive()
    log("Receive$k")
    delay(100)
    val m = channel.receive()
    log("Receive$m")
}

/*
输出结果：
Send:1
Receive1
Send:2
Receive2
Send:3
Receive3
Send:4
Receive4
*/