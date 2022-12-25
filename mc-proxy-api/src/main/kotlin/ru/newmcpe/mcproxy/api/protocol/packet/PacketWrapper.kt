package ru.newmcpe.mcproxy.api.protocol.packet

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import org.apache.logging.log4j.LogManager
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.util.ByteBufUtil
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeVarInt

object PacketWrapper {
    private val logger = LogManager.getLogger()

    private fun wrap(packet: AbstractPacket): ByteArray {
        val allocated = Unpooled.buffer()
        writeVarInt(packet.id.toInt(), allocated)
        packet.write(allocated)
        val wrapped = Unpooled.buffer()
        writeVarInt(allocated.readableBytes(), wrapped)
        wrapped.writeBytes(allocated)
        val bytes = ByteArray(wrapped.readableBytes())
        wrapped.getBytes(0, bytes)
        wrapped.release()
        return bytes
    }

    fun unwrap(buf: ByteBuf, connectionState: ConnectionState): AbstractPacket? {
        val length = ByteBufUtil.readVarInt(buf)
        val id = ByteBufUtil.readVarInt(buf)

        logger.info("Received packet len=$length,id=$id, state=$connectionState")

        val packet = PacketRegistry.getPacket(id.toByte(), connectionState)
        packet?.read(buf)

        return packet
    }

    fun sendPacket(packet: AbstractPacket, channel: Channel) {
        logger.info("Proxy -> Client: $packet")

        channel.writeAndFlush(Unpooled.wrappedBuffer(wrap(packet)))
    }
}