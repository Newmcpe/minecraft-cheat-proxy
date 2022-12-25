package ru.newmcpe.mcproxy

import kotlinx.coroutines.runBlocking
import ru.newmcpe.mcproxy.server.MinecraftServer

object MCProxy {
    lateinit var server: MinecraftServer

    @JvmStatic
    fun main(args: Array<String>) {
        //System.setOut to log4j2 logger

        runBlocking {
            MinecraftServer(25565)
                .also { server = it }
                .also {
                    it
                        .start()
                        .join()
                }
        }
    }
}