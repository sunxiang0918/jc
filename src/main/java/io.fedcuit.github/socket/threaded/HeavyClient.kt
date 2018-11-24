package io.fedcuit.github.socket.threaded

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.locks.LockSupport

class SlowClient : Runnable {
    override fun run() {
        val client = Socket()
        client.connect(InetSocketAddress(8000))
        client.use { socket ->
            PrintWriter(socket.getOutputStream(), true).use { writer ->
                "Hello".forEach {
                    writer.print(it)
                    println("Send to Server: $it")
                    LockSupport.parkNanos(sleepTime)
                }
                writer.println()
                BufferedReader(InputStreamReader(socket.getInputStream())).useLines { lines ->
                    lines.forEach { println("From Server: $it") }
                }
            }
        }
    }

    companion object {
        const val sleepTime = 1000L * 1000 * 1000
    }
}

fun main(args: Array<String>) {
    val tp = Executors.newCachedThreadPool()

    for (i in 1..5) {
        tp.submit(SlowClient())
    }
}