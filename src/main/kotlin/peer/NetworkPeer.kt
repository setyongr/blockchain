package peer

import blockchain.base.BlockChain
import data.db.HostEntity
import data.db.HostTable
import data.model.Block
import data.model.DataItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

class NetworkPeer(private val client: HttpClient, private val blockChain: BlockChain) : Peer {
    private var syncJob: Job? = null

    fun getHost() = transaction { HostEntity.all().map { it.host } }

    fun addHost(host: String) = transaction { HostEntity.new { this.host = host } }

    fun clearHost() = transaction { HostTable.deleteAll() }

    override fun send(data: DataItem) {
        val json = io.ktor.client.features.json.defaultSerializer()
        runBlocking {
            getHost().forEach {
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
        if (getHost().isEmpty()) return
        val currentLastBlock = blockChain.last()
        runBlocking {
            val listBlock = getHost().mapNotNull {
                try {
                    it to client.get<Block>("$it/last_block")
                } catch (e: Exception) {
                    null
                }
            }

            val mostCommonHash = listBlock.groupingBy { it.second.hash }.eachCount().maxBy { it.value }?.key

            val sameCount = listBlock.count { it.second == currentLastBlock }

            if (!blockChain.verifyChain() || sameCount < listBlock.size / 2) {
                // Need To Sync
                val host = listBlock.first { it.second.hash == mostCommonHash }.first
                val newBlockChain = client.get<List<Block>>("$host/blockchain")
                blockChain.replace(newBlockChain)
            }
        }
    }

    override fun notifyBlockAdded(block: Block) {
        GlobalScope.launch {
            val json = io.ktor.client.features.json.defaultSerializer()
            getHost().forEach {
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