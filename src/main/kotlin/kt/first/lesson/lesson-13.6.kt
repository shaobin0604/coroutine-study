package kt.first.lesson

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 协程在多个线程间切换
 */
fun main() = runBlocking(Dispatchers.IO) {
    repeat(3) {
        launch {
            repeat(3) {
                println(Thread.currentThread().name)
                delay(100)
            }
        }
    }

    delay(5000L)
}

/*
输出结果：
DefaultDispatcher-worker-3 @coroutine#2
DefaultDispatcher-worker-2 @coroutine#3
DefaultDispatcher-worker-4 @coroutine#4
DefaultDispatcher-worker-1 @coroutine#2 // 线程切换了
DefaultDispatcher-worker-4 @coroutine#4
DefaultDispatcher-worker-2 @coroutine#3
DefaultDispatcher-worker-2 @coroutine#2 // 线程切换了
DefaultDispatcher-worker-1 @coroutine#4
DefaultDispatcher-worker-4 @coroutine#3

*/