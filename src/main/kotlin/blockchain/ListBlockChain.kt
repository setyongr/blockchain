package blockchain

import blockchain.base.BaseBlockChain
import data.model.Block

@Deprecated("DONT USE, FOR REFERENCE ONLY")
class ListBlockChain : BaseBlockChain() {
    var listBlock = listOf<Block>()

    init {
        listBlock = listBlock + createGenesis()
    }

    override fun add(block: Block) {
        listBlock = listBlock + block
    }

    override fun genesis(): Block = listBlock.first()

    override fun last(): Block = listBlock.last()

    override fun findByIndex(index: Int): Block? = listBlock.firstOrNull { it.index == index }

    override fun findByHash(hash: String): Block? = listBlock.firstOrNull { it.hash == hash }

    override fun prev(block: Block): Block? = findByIndex(block.index - 1)

    override fun next(block: Block): Block? = findByIndex(block.index + 1)

    override fun replace(block: List<Block>) {
        super.replace(block)
        listBlock = block
    }
}