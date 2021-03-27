package com.cout970.modeler.core.network

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by cout970 on 2016/12/20.
 */
class Client() {

    var connection: Connection? = null

    fun sendPacket(packet: Packet) {
        if (connection == null) {
            throw IllegalStateException("Connection closed")
        }
        connection?.send(packet)
    }

    fun onPacket(packet: Packet) {
        println(packet)
    }

    fun connect(serverIp: String) {
        connection = Connection(this, Socket(serverIp, 6864))
        connection!!.runThread()
    }

    override fun toString(): String {
        return "Client()"
    }

    data class Connection(val client: Client, val server: Socket) {

        private val packetQueue = ArrayDeque<Packet>()

        fun send(packet: Packet) {
            packetQueue.addLast(packet)
        }

        fun runThread() {
            thread {
                val outStream = DataOutputStream(server.outputStream)
                val inStream = DataInputStream(server.inputStream)
                while (!server.isClosed && server.isConnected) {
                    while (!packetQueue.isEmpty()) {
                        val packet = packetQueue.removeFirst()
                        val data = packet.encode()

                        outStream.writeInt(data.size)
                        outStream.write(data)
                        outStream.flush()
                    }
                    while (inStream.available() > 0) {
                        val chunkSize = inStream.readInt()
                        val packet = Packet().apply {
                            val data = ByteArray(chunkSize)
                            inStream.readFully(data)
                            decode(data)
                        }
                        client.onPacket(packet)
                    }
                    Thread.yield()
                }
            }
        }
    }
}