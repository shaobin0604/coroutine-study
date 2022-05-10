package kt.first.lesson



/*
 ①    ②      ③            ④
 ↓     ↓       ↓            ↓      */
fun String.lastElement(): Char? {
    //    ⑤
    //    ↓
    if (this.isEmpty()) {
        return null
    }

    return this[length - 1]
}

// 使用扩展函数
fun main() {
    val msg = "Hello Wolrd"
    // lastElement就像String的成员方法一样可以直接调用
    val last = msg.lastElement() // last = d
}