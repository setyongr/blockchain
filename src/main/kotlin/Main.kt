import blockchain.model.Block
import blockchain.ListBlockchain

fun main(args: Array<String>) {
    val blockchain = ListBlockchain()

    blockchain.add("Tes")
    blockchain.add("Tes 2")
    blockchain.add("Tes 3")


    var current: Block<String>? = blockchain.genesis()
    while (current != null) {
        println(current.data)
        current = blockchain.next(current)
    }

    println("Is Valid: ${blockchain.verifyChain()}")

    blockchain.listBlock[1].data = "Changed"
    println("Is Valid: ${blockchain.verifyChain()}")

}