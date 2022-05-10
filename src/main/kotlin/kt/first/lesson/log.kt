package kt.first.lesson

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/** 打印Job的状态信息 */
fun Job.log() {
    logX(
        """
        isActive = $isActive
        isCancelled = $isCancelled
        isCompleted = $isCompleted
        """.trimIndent()
    )
}

/** * 控制台输出带协程信息的log */
fun logX(any: Any?) {
    println(
        """
        ================================$any
        Thread:${Thread.currentThread().name}
        ================================""".trimIndent()
    )
}
