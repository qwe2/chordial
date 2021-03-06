package com.tristanpenman.chordial.demo

import akka.actor.{Actor, ActorRef, Props}
import com.tristanpenman.chordial.core.Event
import spray.can.Http

class WebSocketServer(val governor: ActorRef) extends Actor {
  override def receive: Receive = {
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val conn = context.actorOf(WebSocketWorker.props(serverConnection, governor))
      serverConnection ! Http.Register(conn)

    case e: Event =>
      context.children.foreach { _ ! e }
  }
}

object WebSocketServer {
  def props(governor: ActorRef): Props = Props(new WebSocketServer(governor))
}
