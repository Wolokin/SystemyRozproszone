package edu.agh.reactive.tutorial;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ChatSessionActor extends AbstractBehavior<ChatEvent> {

    public ChatSessionActor(ActorContext<ChatEvent> context) {
        super(context);
    }

    @Override
    public Receive<ChatEvent> createReceive() {
        return newReceiveBuilder().onMessage(
                UserMessage.class, this::onUserMessage
        ).build();
    }

    private Behavior<ChatEvent> onUserMessage(UserMessage message) {
        return Behaviors.same();
    }
}
