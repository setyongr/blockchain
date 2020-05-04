package datapool

import blockchain.base.BlockChain
import data.db.PoolEntity
import data.db.PoolTable
import data.model.DataItem
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import peer.Peer

class DataPool(private val blockChain: BlockChain, private val peer: Peer) {
    private var poolSyncJob: Job? = null

    private var blockDataCount = 3

    fun getPoolItem() = transaction {
        PoolEntity.all().map {
            DataItem(it.data, it.timestamp)
        }
    }

    fun add(data: String) {
        val timestamp = System.currentTimeMillis()
        val item = DataItem(data, timestamp.toString())

        addItem(item)
    }

    fun addItem(item: DataItem) {
       val isNotFound = transaction {
            PoolEntity.find {
                (PoolTable.data eq item.data) and (PoolTable.timestamp eq item.timestamp)
            }.empty()
        }

        if (isNotFound) {
            transaction {
                PoolEntity.new {
                    data = item.data
                    timestamp = item.timestamp
                }
            }
            peer.send(item)
            afterAdd()
        }
    }

    private fun afterAdd() {
        val poolCount = transaction { PoolEntity.count() }
        if (poolCount >= blockDataCount) {
            val dataList = mutableListOf<DataItem>()
            repeat(blockDataCount) {
                val currentData = transaction {
                    PoolEntity.all().orderBy(PoolTable.timestamp to SortOrder.ASC).first()
                }
                dataList.add(DataItem(currentData.data, currentData.timestamp))
                transaction {
                    currentData.delete()
                }
            }

            blockChain.mine(blockChain.createBlock(dataList)) {
                peer.notifyBlockAdded(it)
            }
        }
    }

    fun startSyncJob() {
        poolSyncJob = GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                getPoolItem().forEach {
                    peer.send(it)
                }
                delay(5000)
            }
        }
    }
}