package blockchain

import blockchain.base.BaseBlockchain
import blockchain.db.BlockEntity
import blockchain.db.BlockTable
import blockchain.model.Block
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DBBlockChain(resetTable: Boolean = false) : BaseBlockchain() {

    init {
        val dbName = "blockchain"
        val dbUser = "postgres"
        val dbPassword = ""

        Database.connect(
            "jdbc:postgresql://localhost:5432/${dbName}", driver = "org.postgresql.Driver",
            user = dbUser, password = dbPassword
        )


        transaction {
            if (resetTable) SchemaUtils.drop(BlockTable)
            SchemaUtils.createMissingTablesAndColumns(BlockTable)

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
}