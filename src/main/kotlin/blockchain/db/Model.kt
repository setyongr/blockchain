package blockchain.db

import blockchain.model.Block
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


object BlockTable : IntIdTable() {
    val index = integer("index")
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
                timestamp = block.timestamp
                data = block.data.orEmpty()
                hash = block.hash
                prevHash = block.prevHash
            }
        }
    }

    var index by BlockTable.index
    var timestamp by BlockTable.timestamp
    var data by BlockTable.data
    var hash by BlockTable.hash
    var prevHash by BlockTable.prevHash

    fun toBlock(): Block {
        return Block(index, timestamp, data, hash, prevHash)
    }

}