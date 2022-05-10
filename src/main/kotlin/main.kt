import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor

class MyContinuationInterceptor: ContinuationInterceptor {
    override val key = ContinuationInterceptor
    override fun <T> interceptContinuation(continuation: Continuation<T>) = MyContinuation(continuation)
}

class MyContinuation<T>(val continuation: Continuation<T>): Continuation<T> {
    override val context = continuation.context
    override fun resumeWith(result: Result<T>) {
        log("<MyContinuation> $result" )
        continuation.resumeWith(result)
    }
}

@DelicateCoroutinesApi
suspend fun main() {
    GlobalScope.launch(Dispatchers.Default + MyContinuationInterceptor()) {
        log(1)
        val job = async {
            log(2)
            delay(1000)
            log(3)
            "Hello"
        }
        log(4)
        val result = job.await()
        log("5. $result")
    }.join()
    log(6)
}

fun log(msg: Any?) =
    println("${SimpleDateFormat("HH:mm:ss:SSS").format(Date(System.currentTimeMillis()))} [${Thread.currentThread().name}] $msg")