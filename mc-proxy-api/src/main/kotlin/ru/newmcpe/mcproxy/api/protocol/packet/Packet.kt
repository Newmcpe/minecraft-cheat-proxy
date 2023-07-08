package ru.newmcpe.mcproxy.api.protocol.packet

import io.netty.buffer.ByteBuf

abstract class Packet {
    abstract fun write(buf: ByteBuf)

    abstract fun read(buf: ByteBuf)
}