package ru.newmcpe.mcproxy.api.protocol

class PlayerPublicKey(
    val expiry: Long,
    val key: ByteArray,
    val signature: ByteArray
)