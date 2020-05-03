package data.model

data class PoolItem(
    val data: String,
    val timestamp: String
): Comparable<PoolItem> {
    override fun compareTo(other: PoolItem): Int {
        return timestamp.compareTo(other.timestamp)
    }
}