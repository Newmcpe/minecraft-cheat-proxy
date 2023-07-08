package ru.newmcpe.mcproxy.handling

import kotlinx.coroutines.Job

interface Connection {
    var session: Session?
    fun start(): Job
}