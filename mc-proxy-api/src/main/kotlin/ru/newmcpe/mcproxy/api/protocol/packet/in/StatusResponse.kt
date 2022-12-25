package ru.newmcpe.mcproxy.api.protocol.packet.`in`

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readString
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeString
import java.util.*

class StatusResponse : AbstractPacket(0x00, ConnectionState.STATUS) {
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
            val RANDOM = Status(
                Version("хуй жопа писька пизда", 760),
                Players(
                    500, 100, listOf(
                        Players.Sample("хуй", UUID.randomUUID()),
                        Players.Sample("жопа", UUID.randomUUID()),
                        Players.Sample("пизда", UUID.randomUUID()),
                    )
                ),
                Description("дрочить котам онлайн")
            )
        }
    }
}