package ru.newmcpe.mcproxy.server

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket
import ru.newmcpe.mcproxy.api.protocol.packet.PacketWrapper
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.Ping
import ru.newmcpe.mcproxy.api.protocol.packet.`in`.StatusResponse
import ru.newmcpe.mcproxy.api.protocol.packet.out.Handshake
import ru.newmcpe.mcproxy.client.MinecraftClient
import ru.newmcpe.mcproxy.netty.MinecraftChannelHandler
import ru.newmcpe.mcproxy.netty.NettyConfig
import ru.newmcpe.mcproxy.netty.NettyConfig.eventLoopGroup
import ru.newmcpe.mcproxy.netty.PacketHandler
import java.util.concurrent.Executors

class MinecraftServer(
    var port: Int,
) : PacketHandler {
    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    override lateinit var channel: Channel
    override var state: ConnectionState = ConnectionState.HANDSHAKE
    private val logger = LogManager.getLogger()

    lateinit var clientConnection: MinecraftClient

    override fun start() = scope.launch {
        val bootstrap = ServerBootstrap()
            .option(ChannelOption.SO_REUSEADDR, true)
            .group(eventLoopGroup)
            .channel(NettyConfig.serverChannelClass)
            .childHandler(ServerChildChannelInitializer(this@MinecraftServer))
            .handler(MinecraftChannelHandler())
            .localAddress(port)
            .bind()
            .addListener(listener)

        bootstrap.channel().closeFuture().sync()
    }

    private val listener = ChannelFutureListener { future ->
        if (future.isSuccess) {
            channel = future.channel()
            logger.info("Starting server on ${channel.localAddress()}")
        } else {
            logger.error("Failed to bind to port $port")
        }
    }

    fun handlePacket(packet: AbstractPacket?, ctx: ChannelHandlerContext) {
        when (packet) {
            is Handshake -> {
                if (packet.requestedProtocol == 2) {
                    state = ConnectionState.LOGIN
                    initClientConnection(ctx.channel())
                } else {
                    state = ConnectionState.STATUS

                    val mapper = ObjectMapper()
                    val response = StatusResponse().apply {
                        json = mapper.writeValueAsString(StatusResponse.Status.RANDOM)
                    }

                    PacketWrapper.sendPacket(response, ctx.channel())
                }
            }

            is Ping -> {
                PacketWrapper.sendPacket(packet, ctx.channel())
            }
        }
    }

    private fun initClientConnection(channel: Channel) {
        clientConnection = MinecraftClient("localhost", 25577, channel)
        clientConnection
            .start()
            .invokeOnCompletion {
                PacketWrapper.sendPacket(
                    Handshake().apply {
                        protocolVersion = 5
                        host = "localhost"
                        port = 25577
                        requestedProtocol = 2
                    },
                    clientConnection.channel
                )
            }
    }


    class ServerChildChannelInitializer(private val server: MinecraftServer) : ChannelInitializer<Channel>() {
        override fun initChannel(ch: Channel) {
            ch.pipeline().addLast("handler", ServerPacketInboundHandlerAdapter(server))
        }
    }
}