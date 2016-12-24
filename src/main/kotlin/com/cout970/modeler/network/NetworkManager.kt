package com.cout970.modeler.network

/**
 * Created by cout970 on 2016/12/20.
 */

class NetworkManager {
    fun start() {

    }
}

fun main(args: Array<String>) {

    val server = Server()
    server.listen()

    val client = Client()
    client.connect("localhost")

    client.sendPacket(Packet("<test0>"))
}