package kt.first.lesson

import kotlinx.coroutines.*


// 代码段2

fun main() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        // 变化在这里
        while (isActive) {
            Thread.sleep(500L)
            i ++
            println("i = $i")
        }
    }

    delay(2000L)

    job.cancel()
    job.join()

    println("End")
}

/*
输出结果
i = 1
i = 2
i = 3
i = 4
i = 5
End
*/


