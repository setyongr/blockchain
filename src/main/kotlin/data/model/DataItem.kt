package data.model

data class DataItem(
    val data: String,
    val timestamp: String
): Comparable<DataItem> {
    override fun compareTo(other: DataItem): Int {
        return timestamp.compareTo(other.timestamp)
    }
}