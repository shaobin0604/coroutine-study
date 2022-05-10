package kt.first.lesson

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



// 代码段5

fun main() = runBlocking {
    // 变化在这里
    val channel = Channel<Int>(capacity = Channel.Factory.CONFLATED)
    launch {
        (1..3).forEach {
            channel.send(it)
            println("Send: $it")
        }
        channel.close() // 变化在这里
    }
    launch {
        for (i in channel) {
            println("Receive: $i")
        }
    }
    println("end")
}

/*
输出结果：
end
Send: 1
Send: 2
Send: 3
Receive: 1
Receive: 2
Receive: 3
*/