import java.util.*
import kotlin.random.Random

@Suppress("UNCHECKED_CAST")
fun <T: Comparable<T>> ClosedRange<T>.random(type: Class<out Comparable<*>>): T {
    return when(type) {
        Int::class.java -> Random.nextInt(start.toString().toInt(), endInclusive.toString().toInt()+1) as T
        Long::class.java -> Random.nextLong(start.toString().toLong(), endInclusive.toString().toLong() + 1) as T
        Double::class.java -> Random.nextDouble(start.toString().toDouble(), endInclusive.toString().toDouble()) as T
        else -> throw IllegalArgumentException("Type ${type.simpleName} not supported")
    }
}
fun <T> Set<T>.random(): T?{
    if (isEmpty()) return null
    val index = (Math.random() * size).toInt()
    return elementAt(index)
}

fun randomDate(startDate: Comparable<*>, endDate: Comparable<*>): Date {
    require(startDate is Date && endDate is Date)

    val startMillis = (startDate as Date).time
    val endMillis = (endDate as Date).time
    require(startMillis <= endMillis)

    val randomMillis = startMillis + (Math.random() * (endMillis - startMillis)).toLong()
    return Date(randomMillis)
}