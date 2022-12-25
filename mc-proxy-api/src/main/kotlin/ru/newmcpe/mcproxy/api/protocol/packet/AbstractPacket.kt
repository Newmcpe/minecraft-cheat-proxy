package ru.newmcpe.mcproxy.api.protocol.packet

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState

/**
 * The type Defined packet.
 */
abstract class AbstractPacket(
    val id: Byte = 0x00,
    val state: ConnectionState
) {
    abstract fun write(buf: ByteBuf)

    abstract fun read(buf: ByteBuf)
}