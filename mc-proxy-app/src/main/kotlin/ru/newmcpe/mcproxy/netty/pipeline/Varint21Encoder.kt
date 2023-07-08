package ru.newmcpe.mcproxy.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import ru.newmcpe.mcproxy.api.util.ByteBufUtil.writeVarInt

class Varint21Encoder : MessageToByteEncoder<ByteBuf>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: ByteBuf) {
        val bodyLen = msg.readableBytes()
        val headerLen = varintSize(bodyLen)
        out.ensureWritable(headerLen + bodyLen)
        writeVarInt(bodyLen, out)
        out.writeBytes(msg)
    }

    private fun varintSize(paramInt: Int): Int {
        if (paramInt and -0x80 == 0) return 1
        if (paramInt and -0x4000 == 0) return 2
        if (paramInt and -0x200000 == 0) return 3
        return if (paramInt and -0x10000000 == 0) 4 else 5
    }
}