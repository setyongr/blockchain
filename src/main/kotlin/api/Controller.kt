package api

import blockchain.DBBlockChain
import blockchain.ListBlockchain
import blockchain.base.IBlockChain
import data.model.*
import datapool.DataPool
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import peer.NetworkPeer
import peer.Peer

class Controller {
    @KtorExperimentalAPI
    private val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    private lateinit var blockchain: IBlockChain
    private lateinit var networkPeer: NetworkPeer
    private lateinit var dataPool: DataPool

    fun initController(application: Application) = application.apply {
        val blockStorage = getConfigString("blockChainConfig.blockStorage")
        blockchain = when (blockStorage) {
            "db" -> DBBlockChain(getConfigString("postgresDatabase.resetDB") == "true").apply {
                connect(
                    host = getConfigString("postgresDatabase.host"),
                    port = getConfigString("postgresDatabase.port"),
                    database = getConfigString("postgresDatabase.database"),
                    username = getConfigString("postgresDatabase.username"),
                    password = getConfigString("postgresDatabase.password")
                )
            }
            else -> ListBlockchain()
        }
        networkPeer = NetworkPeer(client, blockchain)
        dataPool = DataPool(blockchain, networkPeer)

        networkPeer.startSyncJob()

        routing {
            showData()
            addData()
            peer()
        }
    }

    @KtorExperimentalAPI
    private fun Application.getConfigString(path: String) = environment.config.propertyOrNull(path)?.getString() ?: ""

    private fun Routing.showData() {
        get("/blockchain") {
            val blocks = mutableListOf<Block>()
            var current: Block? = blockchain.genesis()
            while (current != null) {
                blocks.add(current)
                current = blockchain.next(current)
            }
            call.respond(blocks)
        }

        get("/pool") {
            val poolItems = dataPool.getPoolItem()
            call.respond(poolItems)
        }

        get("/last_block") {
            val lastBlock = blockchain.last()
            call.respond(lastBlock)
        }
    }

    private fun Routing.addData() {
        post("/add") {
            val data = call.receive<AddData>()
            dataPool.add(data.data)
            call.respond(mapOf("OK" to true))
        }

        post("/add_pool_item") {
            val data = call.receive<PoolItem>()
            dataPool.addItem(data)
            call.respond(mapOf("OK" to true))
        }

        post("/notify_new_block") {
            val block = call.receive<Block>()
            blockchain.newBlockFromPeer(block)
        }
    }

    private fun Routing.peer() {
        post("/peer/add") {
            val data = call.receive<AddHost>()
            networkPeer.hosts.add(data.host)
            call.respond(mapOf(
                "hosts" to networkPeer.hosts
            ))
        }

        get("/peer") {
            call.respond(mapOf(
                "hosts" to networkPeer.hosts
            ))
        }

        post("/peer/delete") {
            val data = call.receive<RemoveHost>()
            networkPeer.hosts.removeAt(data.index)
            call.respond(mapOf(
                "hosts" to networkPeer.hosts
            ))
        }

        post("/peer/clear") {
            networkPeer.hosts.clear()
            call.respond(mapOf(
                "hosts" to networkPeer.hosts
            ))
        }
    }
}