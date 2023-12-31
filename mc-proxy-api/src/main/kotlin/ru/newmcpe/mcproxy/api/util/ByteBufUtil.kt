package ru.newmcpe.mcproxy.api.util

import io.netty.buffer.ByteBuf
import ru.newmcpe.mcproxy.api.protocol.PlayerPublicKey
import ru.newmcpe.mcproxy.api.protocol.Property
import java.util.*

object ByteBufUtil {
    fun writeVarInt(value: Int, buf: ByteBuf) {
        var value = value
        while (true) {
            if (value and -0x80 == 0) {
                buf.writeByte(value)
                return
            }
            buf.writeByte(value and 0x7F or 0x80)
            value = value ushr 7
        }
    }

    fun writeString(string: String, buf: ByteBuf) {
        val b: ByteArray = string.toByteArray(Charsets.UTF_8)
        writeVarInt(b.size, buf)
        buf.writeBytes(b)
    }

    fun readVarInt(buf: ByteBuf): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            read = buf.readByte()
            val value = read.toInt() and 0b01111111
            result = result or (value shl 7 * numRead)
            numRead++
            if (numRead > 5) {
                throw RuntimeException("VarInt is too big")
            }
        } while (read.toInt() and 0b10000000 != 0)
        return result
    }


    fun readString(buf: ByteBuf, maxLen: Short): String {
        val len = readVarInt(buf)
        require(len < (maxLen * 3)) { "The received encoded string buffer length is longer than maximum allowed ($len > $maxLen)" }
        val s = buf.toString(buf.readerIndex(), len, Charsets.UTF_8)
        buf.readerIndex(buf.readerIndex() + len)
        require(s.length <= maxLen) { "The received string length is longer than maximum allowed ($len > $maxLen)" }
        return s
    }

    fun readString(buf: ByteBuf) = readString(buf, Short.MAX_VALUE)

    fun writeBoolean(b: Boolean, buf: ByteBuf) = buf.writeBoolean(b)

    fun writeArray(b: ByteArray, buf: ByteBuf) {
        writeVarInt(b.size, buf)
        buf.writeBytes(b)
    }

    fun readArray(buf: ByteBuf): ByteArray {
        val length = readVarInt(buf)
        val bytes = ByteArray(length)
        buf.readBytes(bytes)
        return bytes
    }

    fun writePublicKey(publicKey: PlayerPublicKey, buf: ByteBuf) {
        if (publicKey != null) {
            buf.writeBoolean(true)
            buf.writeLong(publicKey.expiry)
            writeArray(publicKey.key, buf)
            writeArray(publicKey.signature, buf)
        } else {
            buf.writeBoolean(false)
        }
    }

    fun readUUID(input: ByteBuf): UUID = UUID(input.readLong(), input.readLong())

    fun writeUUID(uuid: UUID, output: ByteBuf) {
        output.writeLong(uuid.mostSignificantBits)
        output.writeLong(uuid.leastSignificantBits)
    }

    fun writeProperties(properties: Collection<Property>, buf: ByteBuf) {
        if (properties.isEmpty()) {
            writeVarInt(0, buf)
            return
        }
        writeVarInt(properties.size, buf)
        properties.forEach { prop ->
            writeString(prop.name, buf)
            writeString(prop.value, buf)
            if (prop.signature != null) {
                buf.writeBoolean(true)
                writeString(prop.signature, buf)
            } else {
                buf.writeBoolean(false)
            }
        }
    }

    fun readProperties(buf: ByteBuf): Collection<Property> {
        val properties = arrayOfNulls<Property>(readVarInt(buf))
        for (j in properties.indices) {
            val name = readString(buf)
            val value = readString(buf)
            if (buf.readBoolean()) {
                properties[j] = Property(name, value, readString(buf))
            } else {
                properties[j] = Property(name, value)
            }
        }
        return properties.filterNotNull()
    }

    fun toByteArray(wrapped: ByteBuf): ByteArray {
        val bytes = ByteArray(wrapped.readableBytes())
        wrapped.readBytes(bytes)
        return bytes
    }
}