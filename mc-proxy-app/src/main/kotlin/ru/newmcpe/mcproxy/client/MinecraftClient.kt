package ru.newmcpe.mcproxy.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.socket.SocketChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import ru.newmcpe.mcproxy.api.protocol.ConnectionState
import ru.newmcpe.mcproxy.api.protocol.packet.AbstractPacket
import ru.newmcpe.mcproxy.api.protocol.packet.PacketWrapper
import ru.newmcpe.mcproxy.netty.MinecraftChannelHandler
import ru.newmcpe.mcproxy.netty.NettyConfig.eventLoopGroup
import ru.newmcpe.mcproxy.netty.NettyConfig.socketChannelClass
import ru.newmcpe.mcproxy.netty.PacketHandler
import java.lang.Boolean
import java.util.concurrent.Executors
import kotlin.Int
import kotlin.String


class MinecraftClient(
    private val host: String = "localhost",
    private val port: Int = 25577,
    private val serverChannel: Channel,
) : PacketHandler {
    override lateinit var channel: Channel
    override var state: ConnectionState = ConnectionState.HANDSHAKE
    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    lateinit var bootstrap: ChannelFuture

    private val connectionListener = ChannelFutureListener { c: ChannelFuture ->
        channel = c.channel()
    }

    fun stop() {
        bootstrap.channel().close().sync()
    }

    override fun start() = scope.launch {
        bootstrap = Bootstrap().channel(socketChannelClass).group(eventLoopGroup)
            .option(ChannelOption.TCP_NODELAY, Boolean.TRUE).option(ChannelOption.AUTO_READ, Boolean.TRUE)
            .handler(MinecraftChannelHandler())
            .handler(object : ChannelInitializer<SocketChannel?>() {
                override fun initChannel(ch: SocketChannel?) {
                    ch?.pipeline()?.addLast("handler", ClientPacketInboundHandlerAdapter(this@MinecraftClient))
                }
            })
            .connect(host, port)
            .sync()
            .addListener(connectionListener)
            .sync()
    }

    fun handlePacket(packet: AbstractPacket?) {
        PacketWrapper.sendPacket(packet!!, serverChannel)
    }
}