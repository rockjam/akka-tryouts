package messagePong

import shared.Response

case class Message(text:String, value:Int, status: String = "result") extends Response