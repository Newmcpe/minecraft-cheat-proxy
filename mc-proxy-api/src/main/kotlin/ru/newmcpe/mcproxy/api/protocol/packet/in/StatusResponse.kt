package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString
import java.util.*

class StatusResponse : Packet() {
    lateinit var json: String

    override fun write(buf: ByteBuf) {
        writeString(json, buf)
    }

    override fun read(buf: ByteBuf) {
        json = readString(buf)
    }

    override fun toString(): String {
        return "StatusResponse(json='$json')"
    }

    data class Status(
        val version: Version,
        val players: Players,
        val description: Description,
    ) {
        data class Version(
            val name: String,
            val protocol: Int,
        )

        data class Players(
            val max: Int,
            val online: Int,
            val sample: List<Sample>,
        ) {
            data class Sample(
                val name: String,
                val id: UUID,
            )
        }

        data class Description(
            val text: String,
        )

        companion object {
            val VALUE = Status(
                Version("1.0", 761),
                Players(
                    500, 333, emptyList()
                ),
                Description("Новое имбовое прокси, которое невозможно пофиксить")
            )
        }
    }
}