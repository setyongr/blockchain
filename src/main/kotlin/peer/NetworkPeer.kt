package peer

import blockchain.model.PoolItem

class NetworkPeer: Peer {
    private var hosts = mutableListOf<String>()

    fun addHost(host: String) {
        hosts.add(host)
    }

    override fun send(data: PoolItem) {

    }
}