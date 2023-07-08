package ru.newmcpe.mcproxy.protocol

import io.netty.channel.ChannelHandlerContext
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.Kick
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.LoginSuccess
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.Ping
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.StatusResponse
import ru.newmcpe.mcproxy.api.protocol.packet.out.Handshake
import ru.newmcpe.mcproxy.api.protocol.packet.out.StatusRequest
import ru.newmcpe.mcproxy.netty.pipeline.PacketDecoder
import ru.newmcpe.mcproxy.netty.pipeline.PacketEncoder
import kotlin.reflect.KClass

enum class Protocol {
    HANDSHAKE {
        init {
            toServer.registerPacket(0x00, Handshake::class)
        }
    },
    STATUS {
        init {
            toClient.registerPacket(0x00, StatusResponse::class)
            toClient.registerPacket(0x01, Ping::class)
            toServer.registerPacket(0x00, StatusRequest::class)
            toServer.registerPacket(0x01, Ping::class)
        }
    },

    LOGIN {
        init {
            toClient.registerPacket(0x00, Kick::class)
            toClient.registerPacket(0x02, LoginSuccess::class)
        }
    },
    ;

    val toServer = PacketDirection(PacketDirection.Type.TO_SERVER)
    val toClient = PacketDirection(PacketDirection.Type.TO_CLIENT)

    companion object {
        const val MAX_PACKET_ID = 0xFF
    }

    class PacketDirection(val type: Type) {
        private val packetIdMap: HashMap<KClass<out Packet>, Int> = HashMap(MAX_PACKET_ID)
        private val packetClasses: MutableList<KClass<out Packet>> = mutableListOf()

        fun registerPacket(id: Int, packetClass: KClass<out Packet>) {
            packetIdMap[packetClass] = id
            packetClasses.add(packetClass)
        }

        fun getId(kClass: KClass<out Packet>): Int? {
            return packetIdMap[kClass]/* ?: throw IllegalArgumentException("Packet class $kClass is not registered, direction: $type")*/
        }

        fun changeProtocol(channel: ChannelHandlerContext, newProtocol: Protocol) {
            channel.pipeline()[PacketDecoder::class.java].protocol = newProtocol
            channel.pipeline()[PacketEncoder::class.java].protocol = newProtocol
        }

        fun createPacket(id: Int): Packet? {
            return packetClasses.getOrNull(id)?.constructors?.first()?.call()
        }

        enum class Type {
            TO_SERVER,
            TO_CLIENT
        }
    }
}