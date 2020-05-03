package peer

import data.model.Block
import data.model.PoolItem

interface Peer {
    fun send(data: PoolItem)
    fun sync()
    fun notifyBlockAdded(block: Block)
}