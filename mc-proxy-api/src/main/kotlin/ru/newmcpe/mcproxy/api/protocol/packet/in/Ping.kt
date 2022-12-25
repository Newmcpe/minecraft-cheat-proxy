package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket

class Ping : AbstractPacket(0x01, ConnectionState.STATUS) {
    private var payload: Long = 0

    override fun read(buf: ByteBuf) {
        payload = buf.readLong()
    }

    override fun write(buf: ByteBuf) {
        buf.writeLong(payload)
    }

    override fun toString(): String = "Ping(payload=$payload)"
}