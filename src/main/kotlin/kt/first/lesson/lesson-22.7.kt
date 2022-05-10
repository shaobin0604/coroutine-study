package kt.first.lesson

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicInteger


// 代码段7

fun main() = runBlocking {
    val mutex = Mutex()
    var i = 0
    val jobs = mutableListOf<Job>()

    // 重复十次
    repeat(10){
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                mutex.lock()
                i++
                mutex.unlock()
            }
        }
        jobs.add(job)
    }

    // 等待计算完成
    jobs.joinAll()

    println("i = $i")
}
/*
输出结果
i = 9972
*/


