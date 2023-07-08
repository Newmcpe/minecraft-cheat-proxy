package ru.newmcpe.mcproxy.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.apache.logging.log4j.LogManager
import ru.newmcpe.mcproxy.api.protocol.packet.Packet
import ru.newmcpe.mcproxy.handling.*

class ChannelHandler(private val handler: Connection) : SimpleChannelInboundHandler<Packet>() {
    private val logger = LogManager.getLogger()

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        val session = handler.session!!

        PacketHandler.handle(session, msg)

        when (handler) {
            is ProxyServer -> {
                session.handleServerPacket(msg)
            }

            is ClientConnection -> {
                session.handleClientPacket(msg)
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        if (handler is ProxyServer) {
            logger.info("[+] New incoming connection (Client -> Proxy) from " + ctx.channel().remoteAddress())

            handler.session = Session(handler, ctx.channel())
        } else {
            logger.info("[+] Successfully connected (Proxy -> Server) to " + ctx.channel().remoteAddress())
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        logger.info("[-] Channel disconnected: " + ctx!!.channel().remoteAddress())
        close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        close()
    }

    private fun close() {
        if (handler is ProxyServer) {
            handler.session!!.close()
            handler.session = null
        }
    }
}