package data.db

import data.model.Block
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object BlockTable : IntIdTable() {
    val index = integer("index")
    val nonce = integer("nonce")
    val timestamp = varchar("timestamp", 255)
    val data = text("data")
    val hash = varchar("hash", 255)
    val prevHash = varchar("prev_hash", 255)
}

class BlockEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BlockEntity>(BlockTable) {
        fun newBlock(block: Block) {
            new {
                index = block.index
                nonce = block.nonce
                timestamp = block.timestamp
                data = block.data.orEmpty()
                hash = block.hash
                prevHash = block.prevHash
            }
        }
    }

    var index by BlockTable.index
    var nonce by BlockTable.nonce
    var timestamp by BlockTable.timestamp
    var data by BlockTable.data
    var hash by BlockTable.hash
    var prevHash by BlockTable.prevHash

    fun toBlock(): Block {
        return Block(index, nonce, timestamp, data, hash, prevHash)
    }

}