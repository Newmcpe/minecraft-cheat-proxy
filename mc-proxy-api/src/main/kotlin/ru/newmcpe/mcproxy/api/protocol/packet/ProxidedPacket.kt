package ru.newmcpe.mcproxy.api.protocol.packet

import io.netty.buffer.ByteBuf

class ProxidedPacket(val id: Int) : Packet() {
    var proxy: ByteBuf? = null

    override fun write(buf: ByteBuf) {
        buf.writeBytes(proxy)
    }

    override fun read(buf: ByteBuf) {
        proxy = buf.readBytes(buf.readableBytes())
    }

    override fun toString(): String {
        return "ProxidedPacket(id=$id, buf=$proxy)"
    }
}