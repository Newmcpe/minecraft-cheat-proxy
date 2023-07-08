package ru.newmcpe.mcproxy.handling

import com.fasterxml.jackson.databind.ObjectMapper
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.Ping
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.StatusResponse
import ru.newmcpe.mcproxy.api.protocol.packet.out.Handshake
import ru.newmcpe.mcproxy.api.protocol.packet.out.StatusRequest
import kotlin.reflect.full.functions
import kotlin.reflect.full.starProjectedType

object PacketHandler {
    fun <T : Packet?> handle(session: Session, packet: T): Boolean {
        if (packet == null) return true

//        println(packet)
        return this::class.functions
            .filter { it.name == "handle" }
            .firstOrNull {
                println(packet!!::class.starProjectedType)
                it.parameters[2].type == packet!!::class.starProjectedType
            }
            ?.call(this, session, packet) as Boolean? ?: true
    }

    fun handle(session: Session, packet: StatusRequest): Boolean {
        val objectMapper = ObjectMapper()
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT, false)
        val status = StatusResponse()

        status.json = objectMapper.writeValueAsString(StatusResponse.Status.VALUE)
        session.sendClientPacket(status)

        return false
    }

    fun handle(session: Session, packet: Ping): Boolean {
        session.sendClientPacket(packet)
        return true
    }

    fun handle(session: Session, packet: Handshake): Boolean {
        if (packet.protocolVersion < 760 && packet.requestedProtocol == 2) {
            session.disconnect("{\"text\":\"Proxy only supports versions > 1.19\"}")
        }

        session.startClient(packet)

        return false
    }
}