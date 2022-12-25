package ru.newmcpe.mcproxy.server

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.apache.logging.log4j.LogManager
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.PacketWrapper

class ServerPacketInboundHandlerAdapter(private val server: MinecraftServer) : ChannelInboundHandlerAdapter() {
    private val logger = LogManager.getLogger()

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val packet = PacketWrapper.unwrap(msg as ByteBuf, server.state)
        logger.info("Client -> Proxy: $packet")

        server.handlePacket(packet, ctx)
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        logger.info("Client connected to proxy from ${ctx?.channel()?.remoteAddress()}")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.info("Client disconnected from proxy")
        server.state = ConnectionState.HANDSHAKE
        server.clientConnection.stop()
    }
}