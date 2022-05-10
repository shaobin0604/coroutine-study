package kt.first.lesson

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// 代码段10

fun main() = runBlocking {
    val parentJob: Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null

    parentJob = launch {
        job1 = launch {
            delay(1000L)
        }

        job2 = launch {
            delay(3000L)
        }

        job3 = launch {
            delay(5000L)
        }
    }

    delay(500L)

    parentJob.children.forEachIndexed { index, job ->
        when (index) {
            0 -> println("job1 === job is ${job1 === job}")
            1 -> println("job2 === job is ${job2 === job}")
            2 -> println("job3 === job is ${job3 === job}")
        }
    }

    parentJob.join() // 这里会挂起大约5秒钟
    logX("Process end!")
}

/*
输出结果：
job1 === job is true
job2 === job is true
job3 === job is true
// 等待大约5秒钟
================================
Process end!
Thread:main @coroutine#1
================================
*/