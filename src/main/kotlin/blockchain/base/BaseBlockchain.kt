package blockchain.base

import blockchain.model.Block
import utils.HashUtils

abstract class BaseBlockchain : IBlockChain {
    private fun hashBlock(block: Block): String {
        return HashUtils.sha512("${block.index}${block.timestamp}${block.data}")
    }

    override fun createGenesis(): Block = Block(
        index = 0,
        timestamp = System.currentTimeMillis().toString(),
        data = null,
        prevHash = ""
    ).let { it.copy(hash = hashBlock(it)) }

    override fun createBlock(data: String): Block {
        val lastBlock = last()
        return Block(
            index = lastBlock.index + 1,
            timestamp = System.currentTimeMillis().toString(),
            data = data,
            prevHash = lastBlock.hash
        ).let { it.copy(hash = hashBlock(it)) }
    }

    override fun verifyBlock(block: Block): Boolean {
        return block.hash == hashBlock(block) && block.prevHash == prev(block)?.hash
    }

    override fun verifyChain(): Boolean {
        var current: Block? = next(genesis())
        var valid = true
        while (current != null && valid) {
            valid = verifyBlock(current)
            current = next(current)
        }

        return valid
    }
}