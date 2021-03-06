package data.db

import data.model.Block
import data.model.DataItem
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption


object BlockTable : IntIdTable() {
    val index = integer("index")
    val nonce = integer("nonce")
    val timestamp = varchar("timestamp", 255)
    val hash = varchar("hash", 255)
    val prevHash = varchar("prev_hash", 255)
}

class BlockEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BlockEntity>(BlockTable) {
        fun newBlock(block: Block) {
            val newBlock = new {
                index = block.index
                nonce = block.nonce
                timestamp = block.timestamp
                hash = block.hash
                prevHash = block.prevHash
            }

            block.data.forEach {
                BlockDataEntity.new {
                    this.data = it.data
                    this.timestamp = it.timestamp
                    this.block = newBlock
                }
            }
        }
    }

    var index by BlockTable.index
    var nonce by BlockTable.nonce
    var timestamp by BlockTable.timestamp
    var hash by BlockTable.hash
    var prevHash by BlockTable.prevHash

    fun toBlock(): Block {
        val dataEntities = BlockDataEntity.find { BlockDataTable.block eq this@BlockEntity.id }.toList()
        val dataItem = dataEntities.map {
            DataItem(
                data = it.data,
                timestamp = it.timestamp
            )
        }

        return Block(index, nonce, timestamp, dataItem, hash, prevHash)
    }

}

object BlockDataTable : IntIdTable() {
    val data = text("data")
    val timestamp = varchar("timestamp", 255)

    val block = reference("block", BlockTable, onDelete = ReferenceOption.CASCADE)
}

class BlockDataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BlockDataEntity>(BlockDataTable)

    var data by BlockDataTable.data
    var timestamp by BlockDataTable.timestamp

    var block by BlockEntity referencedOn BlockDataTable.block
}

object PoolTable : IntIdTable() {
    val data = PoolTable.text("data")
    val timestamp = PoolTable.varchar("timestamp", 255)
}

class PoolEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PoolEntity>(PoolTable)

    var data by PoolTable.data
    var timestamp by PoolTable.timestamp
}

object HostTable : IntIdTable() {
    val host = HostTable.text("host")
}

class HostEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HostEntity>(HostTable)

    var host by HostTable.host
}

