package ru.newmcpe.mcproxy.handling

import io.netty.channel.Channel
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.Kick
import ru.newmcpe.mcproxy.api.protocol.packet.out.Handshake

class Session(
    private val server: ProxyServer,
    val channel: Channel
) {
    private val logger = org.apache.logging.log4j.LogManager.getLogger()
    var clientConnection: ClientConnection? = null

    init {
        clientConnection = ClientConnection("localhost", 25577, this)
    }

    fun handleServerPacket(packet: Packet) {
        logger.info("Client -> Proxy: $packet")
    }

    fun handleClientPacket(packet: Packet) {
        logger.info("Server -> Proxy: $packet")
    }

    fun close() {
        channel.close().sync()
        clientConnection!!.stop()
    }

    fun sendClientPacket(packet: Packet) {
        logger.info("Proxy -> Client: $packet")
        channel.writeAndFlush(packet)
    }

    fun sendServerPacket(packet: Packet) {
        logger.info("Proxy -> Server: $packet")
        clientConnection!!.write(packet)
    }

    fun disconnect(reason: String) {
        sendClientPacket(Kick(reason))
        close()
    }

    fun startClient(packet: Handshake) {
        clientConnection!!.start().invokeOnCompletion {
            this.sendServerPacket(packet)
        }
    }
}