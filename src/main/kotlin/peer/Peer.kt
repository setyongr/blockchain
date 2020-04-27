package peer

import blockchain.model.PoolItem

interface Peer {
    fun send(data: PoolItem)
}