import blockchain.ListBlockchain
import blockchain.model.Block
import datapool.DataPool
import peer.NetworkPeer

fun main(args: Array<String>) {
    val blockchain = ListBlockchain()
    blockchain.difficulty = 5

    val networkPeer = NetworkPeer()
    networkPeer.addHost("127.0.0.1")

    val dataPool = DataPool(blockchain, networkPeer)

    dataPool.add("Tes")
    dataPool.add("Tes2")
    dataPool.add("Tes3")
    dataPool.add("Tes4")
    dataPool.add("Tes5")
    dataPool.add("Tes6")
    dataPool.add("Tes7")
    dataPool.add("Tes8")


    // Show Block Chain
    var current: Block? = blockchain.genesis()
    while (current != null) {
        println(current.data)
        current = blockchain.next(current)
    }

    println("Is Valid: ${blockchain.verifyChain()}")
}