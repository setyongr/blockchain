package blockchain.base

import blockchain.model.Block

interface IBlockChain<T> {
    fun getDataString(data: T?): String
    fun createGenesis(): Block<T>
    fun createBlock(data: T): Block<T>
    fun add(data: T)
    fun verifyBlock(block: Block<T>): Boolean
    fun verifyChain(): Boolean
    fun genesis(): Block<T>
    fun last(): Block<T>
    fun findByIndex(index: Int): Block<T>?
    fun findByHash(hash: String): Block<T>?
    fun prev(block: Block<T>): Block<T>?
    fun next(block: Block<T>): Block<T>?
}