package api

import blockchain.ListBlockchain
import data.model.AddData
import data.model.AddHost
import data.model.Block
import data.model.PoolItem
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

class Controller {
    @KtorExperimentalAPI
    private val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    private val blockchain = ListBlockchain()
    private val networkPeer = NetworkPeer(client, blockchain)
    private val dataPool = DataPool(blockchain, networkPeer)

    init {
        networkPeer.startSyncJob()
    }

    fun initRouting(application: Application) = application.apply {
        routing {
            showData()
            addData()
            peer()
        }
    }

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
            networkPeer.addHost(data.host)
            call.respond(mapOf("OK" to true))
        }
    }
}