package datapool

import blockchain.base.IBlockChain
import com.fasterxml.jackson.databind.ObjectMapper
import data.model.PoolItem
import peer.Peer
import utils.HashUtils.sha512
import java.util.*

class DataPool(private val blockChain: IBlockChain, private val peer: Peer) {

    var blockDataCount = 3

    private var pool = PriorityQueue<PoolItem>()

    fun getPoolItem() = pool.toList()

    fun add(data: String) {
        val timestamp = System.currentTimeMillis()
        val item = PoolItem(data, timestamp, sha512(data + timestamp.toString()))

        addItem(item)
    }

    fun addItem(item: PoolItem) {
        if (!pool.contains(item)) {
            pool.add(item)
            peer.send(item)
            afterAdd()
        }
    }

    private fun afterAdd() {
        if (pool.size >= blockDataCount) {
            val dataList = mutableListOf<PoolItem>()
            repeat(blockDataCount) {
                dataList.add(pool.poll())
            }

            val dataStr = ObjectMapper().writeValueAsString(dataList)
            blockChain.mine(blockChain.createBlock(dataStr)) {
                peer.notifyBlockAdded(it)
            }
        }
    }
}