package api

import blockchain.DBBlockChain
import blockchain.base.BlockChain
import data.db.BlockDataTable
import data.db.BlockTable
import data.db.HostTable
import data.db.PoolTable
import data.model.AddData
import data.model.AddHost
import data.model.Block
import data.model.DataItem
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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import peer.NetworkPeer

class Controller {
    @KtorExperimentalAPI
    private val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    private lateinit var blockChain: BlockChain
    private lateinit var networkPeer: NetworkPeer
    private lateinit var dataPool: DataPool

    private fun connectDatabase(
        host: String,
        port: String,
        database: String,
        username: String,
        password: String,
        resetTable: Boolean
    ) {
        Database.connect(
            "jdbc:postgresql://${host}:${port}/${database}", driver = "org.postgresql.Driver",
            user = username, password = password
        )

        transaction {
            if (resetTable) {
                SchemaUtils.drop(BlockTable)
                SchemaUtils.drop(BlockDataTable)
                SchemaUtils.drop(PoolTable)
                SchemaUtils.drop(HostTable)
            }
            SchemaUtils.createMissingTablesAndColumns(BlockTable)
            SchemaUtils.createMissingTablesAndColumns(BlockDataTable)
            SchemaUtils.createMissingTablesAndColumns(PoolTable)
            SchemaUtils.createMissingTablesAndColumns(HostTable)
        }
    }

    fun initController(application: Application) = application.apply {
        connectDatabase(
            host = getConfigString("postgresDatabase.host"),
            port = getConfigString("postgresDatabase.port"),
            database = getConfigString("postgresDatabase.database"),
            username = getConfigString("postgresDatabase.username"),
            password = getConfigString("postgresDatabase.password"),
            resetTable = getConfigString("postgresDatabase.resetDB") == "true"
        )

        blockChain = DBBlockChain()
        blockChain.salt = getConfigString("blockChainConfig.salt")
        blockChain.difficulty = getConfigString("blockChainConfig.difficulty").toInt()

        networkPeer = NetworkPeer(client, blockChain)
        dataPool = DataPool(blockChain, networkPeer)

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
            var current: Block? = blockChain.genesis()
            while (current != null) {
                blocks.add(current)
                current = blockChain.next(current)
            }
            call.respond(blocks)
        }

        get("/pool") {
            val poolItems = dataPool.getPoolItem()
            call.respond(poolItems)
        }

        get("/last_block") {
            val lastBlock = blockChain.last()
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
            val data = call.receive<DataItem>()
            dataPool.addItem(data)
            call.respond(mapOf("OK" to true))
        }

        post("/notify_new_block") {
            val block = call.receive<Block>()
            blockChain.newBlockFromPeer(block)
        }
    }

    private fun Routing.peer() {
        get("/peer") {
            call.respond(
                mapOf(
                    "hosts" to networkPeer.getHost()
                )
            )
        }

        post("/peer/add") {
            val data = call.receive<AddHost>()
            networkPeer.addHost(data.host)
            call.respond(
                mapOf(
                    "hosts" to networkPeer.getHost()
                )
            )
        }

        post("/peer/clear") {
            networkPeer.clearHost()
            call.respond(
                mapOf(
                    "hosts" to networkPeer.getHost()
                )
            )
        }
    }
}