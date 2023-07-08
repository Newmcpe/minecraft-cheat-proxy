package ru.newmcpe.mcproxy.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.protocol.packet.ProxidedPacket
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeVarInt
import ru.newmcpe.mcproxy.protocol.Protocol

class PacketEncoder(
    var protocol: Protocol,
    private val server: Boolean
) : MessageToByteEncoder<Packet>() {

    override fun encode(ctx: ChannelHandlerContext, packet: Packet, out: ByteBuf) = try {
        val direction = if (server) protocol.toClient else protocol.toServer

//        println("Packet is ${packet.javaClass.simpleName}, direction is ${direction.type}")

        val id = direction.getId(packet::class)

        if (id == null) {
            if (packet is ProxidedPacket) {
                writeVarInt(packet.id, out)
            }
        } else {
            writeVarInt(id, out)
        }

        packet.write(out)
    } catch (e: Exception) {
        println("Protocol: ${protocol}, packet: ${packet.javaClass.simpleName}, server: $server")
        e.printStackTrace()
    }
}