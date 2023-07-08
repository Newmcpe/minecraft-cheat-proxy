package ru.newmcpe.mcproxy.handling

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.netty.ChannelHandler
import ru.newmcpe.mcproxy.netty.NettyConfig.eventLoopGroup
import ru.newmcpe.mcproxy.netty.NettyConfig.socketChannelClass
import ru.newmcpe.mcproxy.netty.pipeline.PacketDecoder
import ru.newmcpe.mcproxy.netty.pipeline.PacketEncoder
import ru.newmcpe.mcproxy.netty.pipeline.Varint21Decoder
import ru.newmcpe.mcproxy.netty.pipeline.Varint21Encoder
import ru.newmcpe.mcproxy.protocol.Protocol
import java.util.concurrent.Executors


class ClientConnection(
    private val host: String = "localhost",
    private val port: Int = 25577,
    override var session: Session?
) : Connection {
    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    private var bootstrap: ChannelFuture? = null

    fun stop() {
        bootstrap?.channel()?.close()?.sync()
    }

    override fun start() = scope.launch {
        bootstrap = Bootstrap().channel(socketChannelClass).group(eventLoopGroup)
            .option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.AUTO_READ, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast("varintdecoder",  Varint21Decoder())
                        .addLast("decoder",  PacketDecoder(Protocol.HANDSHAKE, false))

                        .addLast("varintencoder", Varint21Encoder())
                        .addLast("encoder",  PacketEncoder(Protocol.HANDSHAKE, false))

                        .addLast("handler",  ChannelHandler(this@ClientConnection));
                }
            })
            .connect(host, port)
            .sync()
    }

    fun write(packet: Packet) {
        bootstrap?.channel()?.writeAndFlush(packet)?.sync()
    }
}