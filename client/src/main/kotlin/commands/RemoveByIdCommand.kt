package client.commands


import client.net.UDPClient
import common.net.requests.*
import common.net.responses.*
import common.*
import common.entities.LogStatus

class RemoveByIdCommand(val client: UDPClient): Command() {
    override fun getPerm(): LogStatus{
        return LogStatus.LOGGED
    }
    override fun getName() = "remove_by_id"
    override fun execute(argument: String?): Response {
        if (argument == null) throw CommandArgumentException("Method remove_by_id don't support zero arguments")

        val id = argument.toLong()
        return client.sendAndReceiveCommand(UniqueCommandRequest(commandIDc = CommandID.REMOVE_BY_ID, value = id))
    }
}