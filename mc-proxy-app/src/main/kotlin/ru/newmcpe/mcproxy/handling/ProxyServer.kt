package ru.newmcpe.mcproxy.handling

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import ru.newmcpe.mcproxy.netty.ChannelHandler
import ru.newmcpe.mcproxy.netty.NettyConfig
import ru.newmcpe.mcproxy.netty.NettyConfig.eventLoopGroup
import ru.newmcpe.mcproxy.netty.pipeline.PacketDecoder
import ru.newmcpe.mcproxy.netty.pipeline.PacketEncoder
import ru.newmcpe.mcproxy.netty.pipeline.Varint21Decoder
import ru.newmcpe.mcproxy.netty.pipeline.Varint21Encoder
import ru.newmcpe.mcproxy.protocol.Protocol
import java.util.concurrent.Executors

class ProxyServer(
    private var port: Int,
) : Connection {
    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    override var session: Session? = null
    private val logger = org.apache.logging.log4j.LogManager.getLogger()

    override fun start() = scope.launch {
        val bootstrap = ServerBootstrap()
            .option(ChannelOption.SO_REUSEADDR, true)
            .group(eventLoopGroup)
            .channel(NettyConfig.serverChannelClass)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast("varintdecoder", Varint21Decoder())
                        .addLast("decoder", PacketDecoder(Protocol.HANDSHAKE, true))

                        .addLast("varintencoder", Varint21Encoder())
                        .addLast("encoder", PacketEncoder(Protocol.HANDSHAKE, true))

                        .addLast("handler", ChannelHandler(this@ProxyServer));
                }
            })
            .localAddress(port)
            .bind()
            .sync()

        logger.info("Server started on ${bootstrap.channel().localAddress()}")

        bootstrap.channel().closeFuture().sync()
    }
}