package blockchain.model

data class Block(
    var index: Int,
    var timestamp: String = "",
    var data: String?,
    var hash: String = "",
    var prevHash: String = ""
)
