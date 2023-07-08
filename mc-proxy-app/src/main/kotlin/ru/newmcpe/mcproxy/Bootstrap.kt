package ru.newmcpe.mcproxy

import kotlinx.coroutines.runBlocking
import ru.newmcpe.mcproxy.handling.ProxyServer

object Bootstrap {
    lateinit var server: ProxyServer

    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            ProxyServer(25565)
                .also { server = it }
                .also {
                    it
                        .start()
                        .join()
                }
        }
    }
}