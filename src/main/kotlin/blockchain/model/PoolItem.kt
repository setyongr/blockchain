package blockchain.model

data class PoolItem(
    val data: String,
    val timestamp: Long
): Comparable<PoolItem> {
    override fun compareTo(other: PoolItem): Int {
        return timestamp.compareTo(other.timestamp)
    }
}