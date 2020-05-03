package peer

import blockchain.base.IBlockChain
import data.model.Block
import data.model.PoolItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.*

class NetworkPeer(private val client: HttpClient, private val blockchain: IBlockChain) : Peer {
    private var syncJob: Job? = null

    private var hosts = mutableListOf<String>()

    fun addHost(host: String) {
        hosts.add(host)
    }

    fun clearHost() {
        hosts.clear()
    }

    override fun send(data: PoolItem) {
        val json = io.ktor.client.features.json.defaultSerializer()
        runBlocking {
            hosts.forEach {
                try {
                    client.post("$it/add_pool_item") {
                        body = json.write(data)
                    }
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }

    }

    override fun sync() {
        if (hosts.isEmpty()) return
        val currentLastBlock = blockchain.last()
        runBlocking {
            val listBlock = hosts.mapNotNull {
                try {
                    it to client.get<Block>("$it/last_block")
                } catch (e: Exception) {
                    null
                }
            }

            val mostCommonHash = listBlock.groupingBy { it.second.hash }.eachCount().maxBy { it.value }?.key

            val sameCount = listBlock.count { it.second == currentLastBlock }

            if (!blockchain.verifyChain() || sameCount < listBlock.size / 2) {
                // Need To Sync
                val host = listBlock.first { it.second.hash == mostCommonHash }.first
                val newBlockChain = client.get<List<Block>>("$host/blockchain")
                blockchain.replace(newBlockChain)
            }
        }
    }

    override fun notifyBlockAdded(block: Block) {
        GlobalScope.launch {
            val json = io.ktor.client.features.json.defaultSerializer()
            hosts.forEach {
                client.post("$it/notify_new_block") {
                    body = json.write(block)
                }
            }
        }
    }

    fun startSyncJob() {
        syncJob = GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                sync()
                delay(5000)
            }
        }
    }
}