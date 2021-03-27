package com.cout970.modeler.core.network

import com.cout970.modeler.core.log.print
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by cout970 on 2016/12/20.
 */
class Server {

    private val connections: MutableList<Connection> = Collections.synchronizedList(mutableListOf<Connection>())!!

    fun listen() {
        thread {
            val serverSocket = ServerSocket(6864)
            while (true) {
                try {
                    val client = serverSocket.accept()
                    connections += Connection(this, client).apply { runThread() }
                } catch (e: IOException) {
                    e.print()
                }
            }
        }
    }

    fun getConnections(): List<Connection> = connections

    fun onPacket(connection: Connection, packet: Packet) {
        println("$connection: $packet")
        connection.send(Packet("Reply of: $packet"))
    }

    override fun toString(): String {
        return "Server()"
    }

    data class Connection(val server: Server, val client: Socket) {

        private val packetQueue = ArrayDeque<Packet>()

        fun send(packet: Packet) {
            packetQueue.addLast(packet)
        }

        fun runThread() {
            thread {
                val outStream = DataOutputStream(client.outputStream.buffered())
                val inStream = DataInputStream(client.inputStream.buffered())
                while (!client.isClosed && client.isConnected) {
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
                        server.onPacket(this, packet)
                    }
                    Thread.yield()
                }
            }
        }
    }
}

