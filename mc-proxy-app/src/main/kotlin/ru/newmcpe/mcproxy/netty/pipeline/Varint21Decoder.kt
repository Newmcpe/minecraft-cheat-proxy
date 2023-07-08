package ru.newmcpe.mcproxy.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.CorruptedFrameException
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.readVarInt

class Varint21Decoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any?>) {
        if (!ctx.channel().isActive) {
            input.skipBytes(input.readableBytes())
            return
        }
        input.markReaderIndex()
        val buf = ByteArray(3)
        for (i in buf.indices) {
            if (!input.isReadable) {
                input.resetReaderIndex()
                return
            }
            buf[i] = input.readByte()
            if (buf[i] >= 0) {
                val length: Int = readVarInt(Unpooled.wrappedBuffer(buf))
                if (length == 0) {
                    throw CorruptedFrameException("Empty Packet!")
                }
                if (input.readableBytes() < length) {
                    input.resetReaderIndex()
                    return
                } else {
                    if (input.hasMemoryAddress()) {
                        out.add(input.slice(input.readerIndex(), length).retain())
                        input.skipBytes(length)
                    } else {
                        println("Netty is not using direct IO buffers.")

                        // See https://github.com/SpigotMC/BungeeCord/issues/1717
                        val dst = ctx.alloc().directBuffer(length)
                        input.readBytes(dst)
                        out.add(dst)
                    }
                    return
                }
            }
        }
        throw CorruptedFrameException("length wider than 21-bit")
    }
}