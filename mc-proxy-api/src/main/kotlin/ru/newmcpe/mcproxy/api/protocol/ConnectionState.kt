package ru.newmcpe.mcproxy.api.protocol

enum class ConnectionState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY
}