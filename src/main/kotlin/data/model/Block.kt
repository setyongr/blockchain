package data.model

data class Block(
    var index: Int,
    var nonce: Int,
    var timestamp: String = "",
    var data: List<PoolItem> = emptyList(),
    var hash: String = "",
    var prevHash: String = ""
)
