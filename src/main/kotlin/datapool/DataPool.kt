package datapool

import blockchain.base.BaseBlockchain
import blockchain.model.PoolItem
import peer.Peer
import java.util.*

class DataPool(private val blockChain: BaseBlockchain, private val peer: Peer) {

    var blockDataCount = 3

    private var pool = PriorityQueue<PoolItem>()

    fun add(data: String) {
        add(data, System.currentTimeMillis())
    }

    private fun add(data: String, timestamp: Long) {
        val item = PoolItem(data, timestamp)
        pool.add(item)
        peer.send(item)
        afterAdd()
    }

    fun addNoBroadcast(poolItem: PoolItem) {
        pool.add(poolItem)
        afterAdd()
    }

    private fun afterAdd() {
        if (pool.size >= blockDataCount) {
            val dataList = mutableListOf<String>()
            repeat(blockDataCount) {
                dataList.add(pool.poll().data)
            }

            val dataStr = dataList.joinToString(",")
            val minedData = blockChain.mine(blockChain.createBlock(dataStr))
            blockChain.add(minedData)
        }
    }
}