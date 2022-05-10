package kt.first.lesson

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


fun main() {
    runBlocking {                       // 1
        println("Coroutine started!")   // 2
        delay(1000L)                    // 3
        println("Hello World!")         // 4
    }

    println("After launch!")            // 5
    Thread.sleep(2000L)                 // 6
    println("Process end!")             // 7
}

/*
输出结果：
Coroutine started!
Hello World!
After launch!
Process end!
*/