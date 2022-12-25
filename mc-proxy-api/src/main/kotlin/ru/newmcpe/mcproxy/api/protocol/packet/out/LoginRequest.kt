package ru.newmcpe.mcproxy.api.protocol.packet.out

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString

class LoginRequest(
    val data: String,
) : AbstractPacket(0x00, ConnectionState.LOGIN) {
    override fun write(buf: ByteBuf) {
        writeString(data, buf)
        buf.writeBoolean(false)
        buf.writeBoolean(false)
    }

    override fun read(buf: ByteBuf) {
        readString(buf)
        buf.readBoolean()
        buf.readBoolean()
    }

    override fun toString(): String {
        return "LoginRequest(data='$data')"
    }
}