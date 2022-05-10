package kt.first.lesson

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    launch {
        repeat(3) {
            delay(1000L)
            println("Print-1:${Thread.currentThread().name}")
        }
    }

    launch {
        repeat(3) {
            delay(900L)
            println("Print-2:${Thread.currentThread().name}")
        }
    }
    delay(3000L)
}

/*
输出结果：
Print-2:main @coroutine#3
Print-1:main @coroutine#2
Print-2:main @coroutine#3
Print-1:main @coroutine#2
Print-2:main @coroutine#3
Print-1:main @coroutine#2
*/