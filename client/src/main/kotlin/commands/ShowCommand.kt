package client.commands


import client.net.UDPClient
import common.net.requests.*
import common.net.responses.*
import common.*
import common.entities.LogStatus

class ShowCommand(val client: UDPClient): Command() {
    override fun getPerm(): LogStatus{
        return LogStatus.LOGGED
    }
    override fun getName() = "show"
    override fun execute(argument: String?): Response {
        if (argument != null) throw CommandArgumentException("Method show don't support arguments")
        return client.sendAndReceiveCommand(UniqueCommandRequest(commandIDc = CommandID.SHOW))

    }
}