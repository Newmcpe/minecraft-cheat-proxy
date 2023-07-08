package ru.newmcpe.mcproxy.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.buffer.EmptyByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import ru.newmcpe.mcproxy.api.protocol.packet.ProxidedPacket
import ru.newmcpe.mcproxy.api.protocol.packet.out.Handshake
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readVarInt
import ru.newmcpe.mcproxy.protocol.Protocol


class PacketDecoder(
    var protocol: Protocol,
    private val server: Boolean = false
) : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        try {
            if (input is EmptyByteBuf)
                return

            val direction = if (server) protocol.toServer else protocol.toClient

            val id = readVarInt(input)

            val packet = direction.createPacket(id) ?: ProxidedPacket(id)
            packet.read(input)

            if (input.readableBytes() > 0) {
                throw IllegalStateException("Packet ${packet::class.simpleName} has ${input.readableBytes()} bytes left to read")
            }

            out.add(packet)

            if (packet is Handshake) {
                when (packet.requestedProtocol) {
                    1 -> {
                        direction.changeProtocol(ctx, Protocol.STATUS)
                    }

                    2 -> {
                        direction.changeProtocol(ctx, Protocol.LOGIN)
                    }
                }
            }
        } catch (e: Throwable) {
            println("Protocol: ${protocol}, server: $server")
            e.printStackTrace()
        }
    }
}