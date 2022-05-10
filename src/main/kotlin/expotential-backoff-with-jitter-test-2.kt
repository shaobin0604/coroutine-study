import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.LongAdder
import kotlin.math.pow
import kotlin.random.Random

interface RetryPolicy {
    fun getRetryDecision(currentRetryCount: Int, lastException: Exception): RetryDecision
}

data class RetryDecision(val shouldRetry: Boolean, val duration: Long)

class ExponentialBackoffWithJitter1(private val maxDelayMs: Long) : RetryPolicy {
    override fun getRetryDecision(currentRetryCount: Int, lastException: Exception): RetryDecision {
        return if (currentRetryCount == 0) {
            RetryDecision(true, 0)
        } else {
            2.0.pow(currentRetryCount - 1).let { exp ->
                (exp.toLong() * 1000).let {
                    val low = it
                    val high = it * 2
                    RetryDecision(true, (low..high).random().coerceAtMost(maxDelayMs))
                }
            }
        }
    }
}

class ExponentialBackoffWithJitter2(private val maxDelayMs: Long) : RetryPolicy {
    override fun getRetryDecision(currentRetryCount: Int, lastException: Exception): RetryDecision {
        return if (currentRetryCount == 0) {
            RetryDecision(true, 0)
        } else {
            2.0.pow(currentRetryCount - 1).let { exp ->
                (exp.toLong() * 1000).let {
                    val low = 0
                    val high = it
                    RetryDecision(true, 1000 + (low..high).random().coerceAtMost(maxDelayMs))
                }
            }
        }
    }
}

class ExponentialBackoffWithJitterAzure(private val maxDelayMs: Long) : RetryPolicy {
    override fun getRetryDecision(currentRetryCount: Int, lastException: Exception): RetryDecision {
        return if (currentRetryCount == 0) {
            RetryDecision(true, 0)
        } else {
            (2.0.pow(currentRetryCount) - 1).let { exp ->
                exp.toLong().let {
                    // unit: millisecond
                    val low = 600
                    val high = 1400
                    val duration = (1000 + (low..high).random() * it).coerceAtMost(maxDelayMs)
                    RetryDecision(true, duration)
                }
            }
        }
    }
}

/**
 * 该模拟测试程序用于模拟 20000 个客户端在不同重试策略下请求 8 次，统计各秒的请求数
 */
class Client(
    /**
     * 每秒并发访问数 second -> contention count
     * 0 -> 20000 - 第 0 秒时，有 20000 并发访问
     * 1 -> 9997  - 第 1 秒时，有 9997 并发访问
     */
    private val bucket: ConcurrentHashMap<Long, LongAdder>,
    private val maxRetryCount: Int,
    private val algorithmIndex: Int,
    private val id: Long,
    private val debug: Boolean = false,
) {

//    private val random = Random

    suspend fun run() {
        var totalMs = 0L
        for (retry in 0..maxRetryCount) {
            val intervalMs = getDelayFunction(algorithmIndex).invoke(retry).also {
                if (debug) {
                    println("getRetryDelayMs - retry: $retry, delay: $it")
                }
            }
            totalMs += intervalMs
            delay(intervalMs)
            request(retry, totalMs / 1000)
        }
    }

    fun run2() {
        var totalMs = 0L
        for (retry in 0..maxRetryCount) {
            val intervalMs = getDelayFunction(algorithmIndex).invoke(retry).also {
                if (debug) {
                    println("getRetryDelayMs - retry: $retry, delay: $it")
                }
            }
            totalMs += intervalMs
            request(retry, totalMs / 1000)
        }
    }

    private fun getDelayFunction(algorithmIndexes: Int): (Int) -> Long {
        return when (algorithmIndexes) {
            1 -> ::getRetryDelayMs1
            2 -> ::getRetryDelayMs2
            3 -> ::getRetryDelayMs3
            4 -> ::getRetryDelayMs4
            5 -> ::getRetryDelayMs5
            else -> ::getRetryDelayMs6
        }
    }

    private fun request(retryCount: Int, totalSecond: Long) {
        bucket.computeIfAbsent(totalSecond) { LongAdder() }.run {
            increment()
            if (debug) {
                println("${Date()} client: $id, retry: $retryCount, delaySecond: $totalSecond, contention: $this")
            }
        }
    }

    private fun getRetryDelayMs1(retryCount: Int): Long {
        return if (retryCount == 0) {
            0
        } else {
            2.0.pow(retryCount - 1).let { exp ->
                (exp.toLong() * 1000).let {
                    val low = it
                    val high = it * 2
                    (low..high).random().coerceAtMost(60 * 1000)
                }
            }
        }
    }

    // Aws Full Jitter
    private fun getRetryDelayMs2(retryCount: Int): Long {
        return if (retryCount == 0) {
            0
        } else {
            (2.0.pow(retryCount - 1)).let { exp ->
                (exp.toLong() * 2000).let {
                    // unit: millisecond
                    val low = 0
                    val high = it
                    (1000 + (low..high).random()).coerceAtMost(60 * 1000)
                }
            }
        }
    }

    private fun getRetryDelayMs3(retryCount: Int): Long {
        return if (retryCount == 0) {
            0
        } else {
            (2.0.pow(retryCount) - 1).let { exp ->
                exp.toLong().let {
                    // unit: millisecond
                    val low = 800
                    val high = 1200
                    (1000 + (low..high).random() * it).coerceAtMost(60 * 1000)
                }
            }
        }
    }


    // Aws Equal Jitter
    private fun getRetryDelayMs4(retryCount: Int): Long {
        return if (retryCount == 0) {
            0
        } else {
            (2.0.pow(retryCount - 1)).let { exp ->
                exp.toLong().let {
                    // unit: millisecond
                    val low = 0
                    val high = 1000 * it
                    high / 2 + (low..high / 2).random()
                }
            }
        }
    }

    private val rangeMap = mapOf<Int, LongRange>(
        1 to (1000L..3000L),
        2 to (1500L..6000L),
        3 to (5000L..18000L),
        4 to (15000L..54000L),
        5 to (45000L..162000L)
    )

    private fun getRetryDelayMs5(retryCount: Int): Long {
        return if (retryCount == 0) {
            0
        } else {
            if (retryCount > 5) {
                return 162000L
            }
            return rangeMap[retryCount]!!.random()
        }
    }

    private val firstRandom = (0..999L).random()

    private fun getRetryDelayMs6(retryCount: Int): Long {
        return when {
            retryCount == 0 -> {
//                getPredicatePosition(retryCount, firstRandom)
//                firstRandom
                (0..60_000L).random()
            }
            retryCount < 6 -> {
                getPredicatePosition(retryCount, firstRandom) - getPredicatePosition(retryCount - 1, firstRandom)
            }
            retryCount == 6 -> {
                firstRandom * 150 + getLastTopBound(retryCount) - getPredicatePosition(retryCount - 1, firstRandom)
            }
            else -> {
                1000 * 150
            }
        }.coerceAtMost(150 * 1000)
    }

    private fun getPredicatePosition(retryCount: Int, firstRandom: Long): Long {
        return ((2.0.pow(retryCount) * firstRandom) + getLastTopBound(retryCount)).toLong()
    }

    private fun getLastTopBound(retryCount: Int): Long {
        return ((2.0.pow(retryCount) - 1) * 1000).toLong()
    }
}

fun output(bucket: ConcurrentHashMap<Long, LongAdder>) {
    val maxSeconds = bucket.maxOf { it.key }
    (0..maxSeconds).forEach {
        bucket.putIfAbsent(it, LongAdder())
    }
    println(bucket.toList().sortedBy { it.first }.map { it.first })
    println(bucket.toList().sortedBy { it.first }.map { it.second })
}

@OptIn(FlowPreview::class)
suspend fun runSimulation1(bucket: ConcurrentHashMap<Long, LongAdder>, clients: Array<Client>) {
    clients.asFlow()
        .flatMapMerge(clients.size) { client ->
            flow {
                client.run()
                emit(Unit)
            }.flowOn(Dispatchers.Default)
        }.onCompletion {
            output(bucket)
        }.toList()
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun runSimulation2(bucket: ConcurrentHashMap<Long, LongAdder>, clients: Array<Client>) {
    clients.map { client ->
        GlobalScope.async(Dispatchers.Default) {
            client.run()
        }
    }.awaitAll()

    output(bucket)
}

fun runSimulation3(bucket: ConcurrentHashMap<Long, LongAdder>, clients: Array<Client>) {
    clients.forEach { client ->
        client.run2()
    }

    output(bucket)
}

fun main(): Unit = runBlocking {
    val algorithmIndex = 6
    val simulationIndex = 3

    val bucket = ConcurrentHashMap<Long, LongAdder>()
    val clients = Array(20_000) { Client(bucket, 7, algorithmIndex, it.toLong()) }

    when (simulationIndex) {
        1 -> runSimulation1(bucket, clients)
        2 -> runSimulation2(bucket, clients)
        else -> runSimulation3(bucket, clients)
    }
}