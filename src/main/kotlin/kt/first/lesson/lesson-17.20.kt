package kt.first.lesson

import kotlinx.coroutines.*
import java.util.concurrent.Executors


// 代码段20

//  这里使用了挂起函数版本的main()
suspend fun main() {
    val myExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Catch exception: $throwable")
    }

    val mySingleDispatcher =
        Executors.newSingleThreadExecutor { Thread(it, "MySingleThread").apply { isDaemon = true } }
            .asCoroutineDispatcher()

    val scope = CoroutineScope(Job() + mySingleDispatcher + myExceptionHandler)

    val job = scope.launch(CoroutineName("hahah")) {
        val s: String? = null
        s!!.length // 空指针异常
    }

    job.join()
}
/*
输出结果：
Catch exception: java.lang.NullPointerException
*/