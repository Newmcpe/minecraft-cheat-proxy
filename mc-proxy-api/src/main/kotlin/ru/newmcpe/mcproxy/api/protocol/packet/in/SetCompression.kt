package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.packet.Packet

class SetCompression : Packet() {
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