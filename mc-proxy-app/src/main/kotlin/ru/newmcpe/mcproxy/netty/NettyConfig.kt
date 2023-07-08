package ru.newmcpe.mcproxy.netty

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.EventLoopGroup
import io.netty.channel.ServerChannel
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

object NettyConfig {
    val socketChannelClass: Class<out SocketChannel> = NioSocketChannel::class.java
    val serverChannelClass: Class<out ServerChannel> = NioServerSocketChannel::class.java

    val eventLoopGroup: EventLoopGroup =
        if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")) NioEventLoopGroup(
            4,
            createThreadFactory { _: Thread, _: Throwable -> }) else EpollEventLoopGroup(
            4,
            createThreadFactory { _: Thread, _: Throwable -> })

    private fun createThreadFactory(uncaughtExceptionHandler: Thread.UncaughtExceptionHandler?): ThreadFactory {
        val threadFactory = Executors.defaultThreadFactory()
        val atomicLong = AtomicLong(0)
        return ThreadFactory { runnable: Runnable? ->
            val thread = threadFactory.newThread(runnable)
            thread.name = String.format(Locale.ROOT, "PoolThread-%d", atomicLong.getAndIncrement())
            thread.uncaughtExceptionHandler = uncaughtExceptionHandler
            thread.isDaemon = true
            thread
        }
    }

    class MinecraftChannelHandler : ChannelHandler {
        override fun handlerRemoved(handler: ChannelHandlerContext) {
        }

        override fun handlerAdded(handler: ChannelHandlerContext) {
        }

        override fun exceptionCaught(handler: ChannelHandlerContext, t: Throwable) {
            handler.close()
        }
    }
}