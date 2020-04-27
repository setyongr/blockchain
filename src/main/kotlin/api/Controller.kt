package api

import blockchain.ListBlockchain
import blockchain.model.Block
import datapool.DataPool
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import peer.NetworkPeer

@KtorExperimentalAPI
class Controller {
    private val blockchain = ListBlockchain()
    private val networkPeer = NetworkPeer()
    private val dataPool = DataPool(blockchain, networkPeer)

    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    init {
        networkPeer.addHost("127.0.0.1")
    }



    fun initRouting(application: Application) = application.apply {
        routing {
            showBlockChain()
            addData()
        }
    }

    private fun Routing.showBlockChain() {
        get("/blockchain") {
            val blocks = mutableListOf<Block>()
            var current: Block? = blockchain.genesis()
            while (current != null) {
                blocks.add(current)
                current = blockchain.next(current)
            }
            call.respond(blocks)
        }
    }

    private fun Routing.addData() {
        post("/add") {
            val data = call.receive<AddData>()
            dataPool.add(data.data)
            val blocks = client.get<List<Block>>("http://localhost:8080/blockchain")
            call.respond(blocks)
        }
    }
}