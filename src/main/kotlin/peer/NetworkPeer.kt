package peer

import data.model.PoolItem
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class NetworkPeer(private val client: HttpClient) : Peer {
    private var hosts = mutableListOf<String>()

    fun addHost(host: String) {
        hosts.add(host)
    }

    override fun send(data: PoolItem) {
        val json = io.ktor.client.features.json.defaultSerializer()
        runBlocking {
            hosts.map {
                async {
                    client.post<Unit>("$it/add_pool_item") {
                        body = json.write(data)
                    }
                }
            }.forEach { it.await() }
        }

    }
}