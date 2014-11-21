package messagePong

import shared.Exchange

case class Message(text:String, value:Int, status: String = "result") extends Exchange