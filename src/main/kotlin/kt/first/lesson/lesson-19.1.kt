package kt.first.lesson

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// 代码段1

fun main() = runBlocking {
    // 1，创建管道
    val channel = Channel<Int>()

    launch {
        // 2，在一个单独的协程当中发送管道消息
        (1..3).forEach {
            channel.send(it) // 挂起函数
            logX("Send: $it")
        }
    }

    launch {
        // 3，在一个单独的协程当中接收管道消息
        for (i in channel) {  // 挂起函数
            logX("Receive: $i")
        }
    }

    logX("end")
}

/*
================================
end
Thread:main @coroutine#1
================================
================================
Receive: 1
Thread:main @coroutine#3
================================
================================
Send: 1
Thread:main @coroutine#2
================================
================================
Send: 2
Thread:main @coroutine#2
================================
================================
Receive: 2
Thread:main @coroutine#3
================================
================================
Receive: 3
Thread:main @coroutine#3
================================
================================
Send: 3
Thread:main @coroutine#2
================================
// 4，程序不会退出
*/