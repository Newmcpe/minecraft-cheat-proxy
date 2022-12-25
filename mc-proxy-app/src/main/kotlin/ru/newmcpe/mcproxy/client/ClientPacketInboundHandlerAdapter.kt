package ru.newmcpe.mcproxy.client

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.apache.logging.log4j.LogManager
import ru.newmcpe.mcproxy.api.protocol.packet.PacketWrapper

class ClientPacketInboundHandlerAdapter(private val client: MinecraftClient) : ChannelInboundHandlerAdapter() {
    private val logger = LogManager.getLogger()

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val packet = PacketWrapper.unwrap(msg as ByteBuf, client.state)
        logger.info("Server -> Proxy: $packet")

        client.handlePacket(packet)
    }
}