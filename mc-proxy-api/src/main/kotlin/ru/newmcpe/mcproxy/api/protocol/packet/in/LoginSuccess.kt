package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.Property
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readProperties
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readUUID
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeProperties
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeUUID
import java.util.UUID

class LoginSuccess(
    var uuid: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
    var username: String = "",
    var properties: Collection<Property> = mutableListOf()
) : Packet() {

    override fun write(buf: ByteBuf) {
        writeUUID(uuid, buf)
        writeString(username, buf)
        writeProperties(properties, buf)
    }

    override fun read(buf: ByteBuf) {
        uuid = readUUID(buf)
        username = readString(buf)
        properties = readProperties(buf)
    }
}