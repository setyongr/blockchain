package blockchain.base

import blockchain.model.Block

interface IBlockChain {
    fun createGenesis(): Block
    fun createBlock(data: String): Block
    fun add(block: Block)
    fun verifyBlock(block: Block): Boolean
    fun verifyChain(): Boolean
    fun genesis(): Block
    fun last(): Block
    fun findByIndex(index: Int): Block?
    fun findByHash(hash: String): Block?
    fun prev(block: Block): Block?
    fun next(block: Block): Block?
    fun mine(block: Block): Block
}