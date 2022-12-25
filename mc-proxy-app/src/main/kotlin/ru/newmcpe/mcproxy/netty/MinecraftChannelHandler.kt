package ru.newmcpe.mcproxy.netty

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext

class MinecraftChannelHandler : ChannelHandler {
    override fun handlerRemoved(handler: ChannelHandlerContext) {
    }

    override fun handlerAdded(handler: ChannelHandlerContext) {
    }

    override fun exceptionCaught(handler: ChannelHandlerContext, t: Throwable) {
        handler.close()
    }
}