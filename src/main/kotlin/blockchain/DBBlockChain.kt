package blockchain

import blockchain.base.BaseBlockChain
import data.db.BlockDataEntity
import data.db.BlockDataTable
import data.db.BlockEntity
import data.db.BlockTable
import data.model.Block
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class DBBlockChain(private val resetTable: Boolean = false) : BaseBlockChain() {

    fun connect(host: String, port: String, database: String, username: String, password: String) {
        Database.connect(
            "jdbc:postgresql://${host}:${port}/${database}", driver = "org.postgresql.Driver",
            user = username, password = password
        )

        transaction {
            if (resetTable) {
                SchemaUtils.drop(BlockTable)
                SchemaUtils.drop(BlockDataTable)
            }
            SchemaUtils.createMissingTablesAndColumns(BlockTable)
            SchemaUtils.createMissingTablesAndColumns(BlockDataTable)

            if (BlockEntity.count() == 0) {
                BlockEntity.newBlock(createGenesis())
            }
        }
    }

    override fun add(block: Block) {
        transaction {
            BlockEntity.newBlock(block)
        }
    }

    override fun genesis(): Block = transaction { BlockEntity.all().first().toBlock() }

    override fun last(): Block = transaction { BlockEntity.all().maxBy { it.index }!!.toBlock() }

    override fun findByIndex(index: Int): Block? =
        transaction { BlockEntity.find { BlockTable.index eq index }.firstOrNull()?.toBlock() }

    override fun findByHash(hash: String): Block? = transaction {
        BlockEntity.find { BlockTable.hash eq hash }.firstOrNull()?.toBlock()
    }

    override fun prev(block: Block): Block? = transaction { findByIndex(block.index - 1) }

    override fun next(block: Block): Block? = transaction { findByIndex(block.index + 1) }

    override fun replace(block: List<Block>) {
        super.replace(block)
        transaction {
            BlockTable.deleteAll()
            block.forEach {
                BlockEntity.newBlock(it)
            }
        }
    }
}