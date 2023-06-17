package client.commands


import client.builders.MovieBuilder
import client.net.UDPClient
import common.net.requests.*
import common.net.responses.*
import common.*
import common.entities.LogStatus

class InfoCommand(val client: UDPClient): Command() {
    override fun getPerm(): LogStatus{
        return LogStatus.LOGGED
    }
    override fun getName() =  "info"
    override fun execute(argument: String?): Response {
        if (argument != null) throw CommandArgumentException("Method info don't support arguments")
        val response = client.sendAndReceiveCommand(UniqueCommandRequest(commandIDc = CommandID.INFO))
        return response

    }
}