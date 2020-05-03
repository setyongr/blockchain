package blockchain.base

import data.model.Block
import jdk.nashorn.internal.runtime.GlobalConstants
import kotlinx.coroutines.*
import utils.HashUtils

abstract class BaseBlockchain : IBlockChain {
    private var difficulty = 3
    var miningJob: Job? = null

    private fun hashBlock(block: Block): String {
        return HashUtils.sha512("WowBlock${block.nonce}${block.index}${block.timestamp}${block.data}")
    }

    override fun createGenesis(): Block = Block(
        index = 0,
        nonce = 0,
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
            prevHash = lastBlock.hash,
            nonce = 0
        )
    }

    override fun mine(block: Block, onFinish: (block: Block) -> Unit) {
        miningJob = GlobalScope.launch {
            var hash = hashBlock(block)
            val key = "0".repeat(difficulty)
            while (isActive && hash.substring(0, difficulty) != key) {
                block.nonce += 1
                hash = hashBlock(block)
            }

            if (isActive) {
                block.hash = hash
                add(block)
                onFinish(block)
            }
        }
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

    override fun newBlockFromPeer(block: Block) {
        miningJob?.cancel()
        if (last().hash == block.prevHash) {
            add(block)
        }
    }
}