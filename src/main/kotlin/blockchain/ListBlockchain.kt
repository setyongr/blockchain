package blockchain

import blockchain.base.BaseBlockchain
import blockchain.model.Block

class ListBlockchain : BaseBlockchain<String>() {
    var listBlock = listOf<Block<String>>()

    init {
        listBlock = listBlock + createGenesis()
    }

    override fun add(data: String) {
        listBlock = listBlock + createBlock(data)
    }

    override fun genesis(): Block<String> = listBlock.first()

    override fun last(): Block<String> = listBlock.last()

    override fun findByIndex(index: Int): Block<String>? = listBlock.firstOrNull { it.index == index }

    override fun findByHash(hash: String): Block<String>? = listBlock.firstOrNull { it.hash == hash }

    override fun prev(block: Block<String>): Block<String>? = findByIndex(block.index - 1)

    override fun next(block: Block<String>): Block<String>? = findByIndex(block.index + 1)

    override fun getDataString(data: String?): String = data.orEmpty()
}