package kt.first.lesson

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking


// 代码段3

fun main() = runBlocking {
    // Flow转List
    flowOf(1, 2, 3, 4, 5)
        .toList()
        .filter { it > 2 }
        .map { it * 2 }
        .take(2)
        .forEach {
            println(it)
        }

    // List转Flow
    listOf(1, 2, 3, 4, 5)
        .asFlow()
        .filter { it > 2 }
        .map { it * 2 }
        .take(2)
        .collect {
            println(it)
        }
}

/*
输出结果
6
8
6
8
*/