package ru.newmcpe.mcproxy.api.protocol.packet.out

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.packet.Packet

class StatusRequest : Packet() {
    override fun read(buf: ByteBuf) {
        // Nothing to read
    }

    override fun write(buf: ByteBuf) {
        // Nothing to write
    }

    override fun toString(): String {
        return "StatusRequest()"
    }
}