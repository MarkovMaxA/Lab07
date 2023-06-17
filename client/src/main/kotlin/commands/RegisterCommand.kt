package client.commands


import client.builders.UserBuilder
import client.net.UDPClient
import common.net.requests.*
import common.*
import common.entities.LogStatus
import common.net.responses.Response

class RegisterCommand(val client: UDPClient): Command() {
    override fun getPerm(): LogStatus{
        return LogStatus.NEEDLOGIN
    }
    override fun getName() = "register"
    override fun execute(argument: String?): Response {
        if (argument != null) throw CommandArgumentException("Method login don't support arguments")
        val user= UserBuilder.build()
        return client.sendAndReceiveCommand(UniqueCommandRequest(commandIDc = CommandID.REGISTER,user=user))
    }
}