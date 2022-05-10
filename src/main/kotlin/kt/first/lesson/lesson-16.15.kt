package kt.first.lesson

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// 代码段15

fun main() = runBlocking {
    val job = launch {
        logX("First coroutine start!")
        delay(1000L)
        logX("First coroutine end!")
    }

    job.join()
    val job2 = launch(job) {
        logX("Second coroutine start!")
        delay(1000L)
        logX("Second coroutine end!")
    }
    job2.join()
    logX("Process end!")
}