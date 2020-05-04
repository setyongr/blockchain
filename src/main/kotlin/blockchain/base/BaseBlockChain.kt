package blockchain.base

import data.model.Block
import data.model.DataItem
import kotlinx.coroutines.*
import utils.HashUtils

abstract class BaseBlockChain : BlockChain {
    override var difficulty = 3
    override var salt = "WoWBlock"

    private var miningJob: Job? = null

    private fun hashBlock(block: Block): String {
        val dataHashList = block.data.joinToString {
            HashUtils.sha256(it.data + it.timestamp)
        }
        return HashUtils.sha512("${salt}${block.nonce}${block.index}${block.timestamp}${dataHashList}")
    }

    override fun createGenesis(): Block = Block(
        index = 0,
        nonce = 0,
        timestamp = "0",
        data = emptyList(),
        prevHash = ""
    ).let { it.copy(hash = hashBlock(it)) }

    override fun createBlock(data: List<DataItem>): Block {
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

    override fun replace(block: List<Block>) {
        miningJob?.cancel()
    }

    override fun newBlockFromPeer(block: Block) {
        miningJob?.cancel()
        if (last().hash == block.prevHash) {
            add(block)
        }
    }
}