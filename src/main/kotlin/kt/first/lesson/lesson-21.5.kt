package kt.first.lesson

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select


// 代码段4
data class Product(
    val productId: String,
    val price: Double,
    // 是不是缓存信息
    val isCache: Boolean = false
)

// 代码段5
fun main() = runBlocking {
    suspend fun getCacheInfo(productId: String): Product? {
        logX("getCacheInfo E")
        delay(100L)
        return Product(productId, 9.9)
    }

    suspend fun getNetworkInfo(productId: String): Product? {
        logX("getNetworkInfo E")
        delay(200L)
        return Product(productId, 9.8)
    }

    fun updateUI(product: Product) {
        println("${product.productId}==${product.price}")
    }

    val startTime = System.currentTimeMillis()
    val productId = "xxxId"

    // 1，缓存和网络，并发执行
    val cacheDeferred = async { getCacheInfo(productId) }
    val latestDeferred = async { getNetworkInfo(productId) }

    // 2，在缓存和网络中间，选择最快的结果
    val product = select<Product?> {
        cacheDeferred.onAwait {
            it?.copy(isCache = true)
        }

        latestDeferred.onAwait {
            it?.copy(isCache = false)
        }
    }

    // 3，更新UI
    if (product != null) {
        updateUI(product)
        logX("Time cost: ${System.currentTimeMillis() - startTime}")
    }

    // 4，如果当前结果是缓存，那么再取最新的网络服务结果
    if (product != null && product.isCache) {
        val latest = latestDeferred.await() ?: return@runBlocking
        updateUI(latest)
        logX("Time cost: ${System.currentTimeMillis() - startTime}")
    }
}

/*
输出结果：
xxxId==9.9
Time cost: 120
xxxId==9.8
Time cost: 220
*/