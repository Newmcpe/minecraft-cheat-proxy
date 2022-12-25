package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readArray
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeArray
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString

class EncryptionRequest : AbstractPacket(0x01, ConnectionState.LOGIN) {
    lateinit var serverId: String
    lateinit var publicKey: ByteArray
    lateinit var verifyToken: ByteArray

    override fun read(buf: ByteBuf) {
        serverId = readString(buf, 255)
        publicKey = readArray(buf)
        verifyToken = readArray(buf)
    }

    override fun write(buf: ByteBuf) {
        writeString(serverId, buf)
        writeArray(publicKey, buf)
        writeArray(verifyToken, buf)
    }

    override fun toString(): String {
        return "EncryptionRequest(serverId='$serverId', publicKey=${publicKey.contentToString()}, verifyToken=${verifyToken.contentToString()})"
    }
}