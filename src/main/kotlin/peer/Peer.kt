package peer

import data.model.PoolItem

interface Peer {
    fun send(data: PoolItem)
}