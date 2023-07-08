package ru.newmcpe.mcproxy.api.protocol

data class Property(
    val name: String,
    val value: String,
    val signature: String? = null
)