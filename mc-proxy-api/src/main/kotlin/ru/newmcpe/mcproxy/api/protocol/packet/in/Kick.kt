package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString

data class Kick(
    var reason: String = ""
) : Packet() {

    override fun write(buf: ByteBuf) {
        writeString(reason, buf)
    }

    override fun read(buf: ByteBuf) {
        readString(buf)
    }
}