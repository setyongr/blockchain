package blockchain.base

import data.model.Block
import data.model.DataItem

interface BlockChain {
    var salt: String
    var difficulty: Int

    fun createGenesis(): Block
    fun createBlock(data: List<DataItem>): Block
    fun add(block: Block)
    fun verifyBlock(block: Block): Boolean
    fun verifyChain(): Boolean
    fun genesis(): Block
    fun last(): Block
    fun findByIndex(index: Int): Block?
    fun findByHash(hash: String): Block?
    fun prev(block: Block): Block?
    fun next(block: Block): Block?
    fun mine(block: Block, onFinish: (block: Block) -> Unit)
    fun replace(block: List<Block>)
    fun newBlockFromPeer(block: Block)
}