import kotlin.math.pow

data class Row(val retryCount: Int, val intervalMin: Long, val intervalMax: Long)
data class AccRow(val row: Row, val accMin: Long, val accMax: Long)

fun getRetryRow(retryCount: Int): Row {
    return if (retryCount == 0) {
        Row(retryCount, 0, 0)
    } else {
        2.0.pow((retryCount - 1).toDouble()).let {
            Row(retryCount, it.toLong(), (it + 1).toLong())
        }
    }
}

fun getRetryRow2(retryCount: Int): Row {
    return if (retryCount == 0) {
        Row(retryCount, 0, 0)
    } else {
        2.0.pow((retryCount - 1).toDouble()).let {
            Row(retryCount, it.toLong().coerceAtMost(60), (it * 2).toLong().coerceAtMost(60))
        }
    }
}

fun getRetryRow3(retryCount: Int): Row {
    return if (retryCount == 0) {
        Row(retryCount, 0, 0)
    } else {
        (2.0.pow(retryCount.toDouble()) - 1).let {
            Row(retryCount, 1000 + it.toLong() * 600, 1000 + it.toLong() * 1400)
        }
    }
}


fun main() {

    val rows = mutableListOf<Row>()

    for (retryCount in 0..10) {
        rows.add(getRetryRow2(retryCount))
    }

    val accRows = mutableListOf<AccRow>()

    var accMin = 0L
    var accMax = 0L

    for (row in rows) {
        accMin += row.intervalMin
        accMax += row.intervalMax

        accRows.add(AccRow(row, accMin, accMax))
    }

    for (accRow in accRows) {
        println(accRow)
    }
}