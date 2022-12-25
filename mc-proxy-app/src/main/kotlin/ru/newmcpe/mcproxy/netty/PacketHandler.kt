package ru.newmcpe.mcproxy.netty

import io.netty.channel.Channel
import kotlinx.coroutines.Job
import ru.newmcpe.mcproxy.api.protocol.ConnectionState

interface PacketHandler {
    var channel: Channel
    var state: ConnectionState

    fun start(): Job
}