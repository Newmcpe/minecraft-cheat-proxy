package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket

class SetCompression : AbstractPacket(
    0x03,
    ConnectionState.LOGIN
) {
    var threshold: Int = 0

    override fun read(buf: ByteBuf) {
        threshold = buf.readInt()
    }

    override fun write(buf: ByteBuf) {
        buf.writeInt(threshold)
    }

    override fun toString(): String {
        return "SetCompression(threshold=$threshold)"
    }
}