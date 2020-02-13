package blockchain.base

import blockchain.model.Block
import utils.HashUtils

abstract class BaseBlockchain<T> : IBlockChain<T> {
    private fun hashBlock(block: Block<T>): String {
        return HashUtils.sha512("${block.index}${block.timestamp}${getDataString(block.data)}")
    }

    override fun createGenesis(): Block<T> = Block<T>(
        index = 0,
        timestamp = System.currentTimeMillis().toString(),
        data = null,
        prevHash = ""
    ).let { it.copy(hash = hashBlock(it)) }

    override fun createBlock(data: T): Block<T> {
        val lastBlock = last()
        return Block(
            index = lastBlock.index + 1,
            timestamp = System.currentTimeMillis().toString(),
            data = data,
            prevHash = lastBlock.hash
        ).let { it.copy(hash = hashBlock(it)) }
    }

    override fun verifyBlock(block: Block<T>): Boolean {
        return block.hash == hashBlock(block) && block.prevHash == prev(block)?.hash
    }

    override fun verifyChain(): Boolean {
        var current: Block<T>? = next(genesis())
        var valid = true
        while (current != null && valid) {
            valid = verifyBlock(current)
            current = next(current)
        }

        return valid
    }
}