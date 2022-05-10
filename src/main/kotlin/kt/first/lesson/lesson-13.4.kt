package kt.first.lesson

import kotlin.concurrent.thread


// 代码中一共启动了两个线程
fun main() {
    println(Thread.currentThread().name)
    thread {
        println(Thread.currentThread().name)
        Thread.sleep(100)
    }
    Thread.sleep(1000L)
}

/*
输出结果：
main
Thread-0
*/