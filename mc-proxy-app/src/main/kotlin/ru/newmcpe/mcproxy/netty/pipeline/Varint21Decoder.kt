package ru.newmcpe.mcproxy.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.CorruptedFrameException
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readVarInt

class Varint21Decoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any?>) {
        if (!ctx.channel().isActive) {
            `in`.skipBytes(`in`.readableBytes())
            return
        }
        `in`.markReaderIndex()
        val buf = ByteArray(3)
        for (i in buf.indices) {
            if (!`in`.isReadable) {
                `in`.resetReaderIndex()
                return
            }
            buf[i] = `in`.readByte()
            if (buf[i] >= 0) {
                val length: Int = readVarInt(Unpooled.wrappedBuffer(buf))
                if (length == 0) {
                    throw CorruptedFrameException("Empty Packet!")
                }
                if (`in`.readableBytes() < length) {
                    `in`.resetReaderIndex()
                    return
                } else {
                    if (`in`.hasMemoryAddress()) {
                        out.add(`in`.slice(`in`.readerIndex(), length).retain())
                        `in`.skipBytes(length)
                    } else {
                        println("Netty is not using direct IO buffers.")

                        // See https://github.com/SpigotMC/BungeeCord/issues/1717
                        val dst = ctx.alloc().directBuffer(length)
                        `in`.readBytes(dst)
                        out.add(dst)
                    }
                    return
                }
            }
        }
        throw CorruptedFrameException("length wider than 21-bit")
    }
}