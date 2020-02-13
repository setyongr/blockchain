package blockchain.model

data class Block<T>(
    var index: Int,
    var timestamp: String = "",
    var data: T?,
    var hash: String = "",
    var prevHash: String = ""
)