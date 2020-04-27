package data.model

data class PoolItem(
    val data: String,
    val timestamp: Long,
    val hash: String
): Comparable<PoolItem> {
    override fun compareTo(other: PoolItem): Int {
        return timestamp.compareTo(other.timestamp)
    }
}