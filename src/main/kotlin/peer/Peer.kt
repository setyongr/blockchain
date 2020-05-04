package peer

import data.model.Block
import data.model.DataItem

interface Peer {
    fun send(data: DataItem)
    fun sync()
    fun notifyBlockAdded(block: Block)
}