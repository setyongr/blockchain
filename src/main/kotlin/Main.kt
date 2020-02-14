import blockchain.DBBlockChain
import blockchain.model.Block

fun main(args: Array<String>) {
    val blockchain = DBBlockChain()

    blockchain.add("Tes 1")
    blockchain.add("Tes 2")
    blockchain.add("Tes 3")

    var current: Block? = blockchain.genesis()
    while (current != null) {
        println(current.data)
        current = blockchain.next(current)
    }

    println("Is Valid: ${blockchain.verifyChain()}")
}