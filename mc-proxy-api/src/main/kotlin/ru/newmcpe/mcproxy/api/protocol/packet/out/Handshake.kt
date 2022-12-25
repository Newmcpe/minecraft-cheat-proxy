package ru.newmcpe.mcproxy.api.protocol.packet.out

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readVarInt
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeVarInt

class Handshake : AbstractPacket(0x00, ConnectionState.HANDSHAKE) {
    var protocolVersion: Int = 0
    lateinit var host: String
    private var port: Int = 25565
    var requestedProtocol: Int = 2

    override fun write(buf: ByteBuf) {
        writeVarInt(protocolVersion, buf)
        writeString(host, buf)
        buf.writeShort(port)
        writeVarInt(requestedProtocol, buf)
    }

    override fun read(buf: ByteBuf) {
        protocolVersion = readVarInt(buf)
        host = readString(buf)
        port = buf.readUnsignedShort()
        requestedProtocol = readVarInt(buf)
    }

    override fun toString(): String {
        return "Handshake(protocolVersion=$protocolVersion, host='$host', port=$port, requestedProtocol=$requestedProtocol)"
    }
}