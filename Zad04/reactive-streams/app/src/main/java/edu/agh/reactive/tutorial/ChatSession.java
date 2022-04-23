package edu.agh.reactive.tutorial;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.concurrent.Future;

public class ChatSession {
    String userId;
    ActorSystem system;

    public ChatSession(String userId, ActorSystem system) {
        this.userId = userId;
        this.system = system;
    }

//    private Future<ActorRef> ask() {
//        system.actorSelection()
//    }
}
