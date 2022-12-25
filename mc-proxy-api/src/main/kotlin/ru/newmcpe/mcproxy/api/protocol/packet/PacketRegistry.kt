package ru.newmcpe.mcproxy.api.protocol.packet

import org.apache.logging.log4j.LogManager
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.EncryptionRequest
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.Ping
import ru.newmcpe.mcproxy.api.protocol.packet.out.Handshake
import kotlin.reflect.KClass

object PacketRegistry {
    private val packets = mutableMapOf<Pair<Byte, ConnectionState>, KClass<out AbstractPacket>>()
    private val logger = LogManager.getLogger()

    init {
        register(EncryptionRequest::class, 0x01, ConnectionState.LOGIN)
        register(Handshake::class, 0x00, ConnectionState.HANDSHAKE)
        register(Ping::class, 0x01, ConnectionState.STATUS)
    }

    private fun register(clazz: KClass<out AbstractPacket>, id: Byte, state: ConnectionState) {
        packets[id to state] = clazz
    }

    fun getPacket(id: Byte, state: ConnectionState): AbstractPacket? {
        val clazz = packets[id to state] ?: return null

        return clazz.constructors.first().call()
    }
}