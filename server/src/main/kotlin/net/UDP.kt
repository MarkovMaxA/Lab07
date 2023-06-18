package net

import commands.CommandManager
import common.CommandID
import common.net.requests.*
import common.net.responses.UniqueCommandResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.*
import managers.DataBaseManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask

class ForkJoinTask(private val request: UniqueCommandRequest, private val commandManager: CommandManager) : RecursiveTask<UniqueCommandResponse>() {
    override fun compute(): UniqueCommandResponse {
        return commandManager.handle(request)
    }
}
abstract class UDP(var address: InetSocketAddress, val commandManager: CommandManager, private val dataBaseManager: DataBaseManager) {
    protected val logger: Logger = LoggerFactory.getLogger(UDP::class.java)
    private var runFlag = true

    fun getAddr() = address

    abstract fun receive(): Pair<ByteArray, SocketAddress?>

    abstract fun send(data: ByteArray, address: SocketAddress)

    abstract fun connect(address: SocketAddress)

    abstract fun disconnect()

    abstract fun close()

    fun stop() {
        runFlag = !runFlag
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun run() {
        logger.info("Server started, address=$address")

        while (runFlag) {
            var data: Pair<ByteArray, SocketAddress?>

            try {
                data = receive()
                if (data.second == null) {
                    logger.error("Receiving data error")
                    disconnect()
                    continue
                }
            } catch (e: Exception) {
                logger.error("Receiving data error ${e.toString()}", e)
                disconnect()
                continue
            }

            try {
                connect(data.second!!)
                logger.info("Server connected to client address=${data.second}")
            } catch (e: Exception) {
                logger.error("Client connection error $e", e)
            }

            val request: UniqueCommandRequest

            try {
                request = ProtoBuf.decodeFromByteArray(data.first)
                logger.info("Request $request handle")
            } catch (e: Exception) {
                logger.error("Unnable to serialize request $e", e)
                disconnect()
                continue
            }

            val response: UniqueCommandResponse

            try {
                if (request.commandID != CommandID.REGISTER)
                    if (!dataBaseManager.confirmUser(request.user!!))
                        throw Exception("User auth failed")
                response = commandManager.handle(request)
                logger.info("Created response $response")
            } catch (e: Exception) {
                logger.error("Command error $e", e)
                continue
            }

            //val task = ForkJoinTask(request, commandManager)
            //val pool = ForkJoinPool()
            //val responseFork = pool.invoke(task)
            //pool.shutdown()

            val dataToSend = ProtoBuf.encodeToByteArray(response)
            logger.info("Ответ: $response")

            try {
                send(dataToSend, data.second!!)
                logger.info("Отправлен ответ клиенту ${data.second}")
            } catch (e: java.lang.Exception) {
                logger.error("Ошибка ввода-вывода : $e", e)
            }

            disconnect()
            logger.info("Сервер отключен")
        }

        close()
    }
}