package edu.agh.reactive.chat;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.Pair;
import akka.stream.javadsl.*;
import akka.util.ByteString;

public class Chat {
    static void chat() {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.receive(Void.class)
                .onSignal(Terminated.class, sig -> Behaviors.stopped())
                .build(), "System");
        // #chat
        Pair<Sink<String, NotUsed>, Source<String, NotUsed>> pair =
                MergeHub.of(String.class).toMat(BroadcastHub.of(String.class), Keep.both()).run(system);
        Sink<String, NotUsed> sink = pair.first();
        Source<String, NotUsed> source = pair.second();

        Flow<ByteString, ByteString, NotUsed> framing =
                Framing.delimiter(ByteString.fromString("\n"), 1024);

        Sink<ByteString, NotUsed> sinkWithFraming =
                framing.map(s -> s.utf8String() + "witam").to(sink);
        Source<ByteString, NotUsed> sourceWithFraming =
                source.map(text -> ByteString.fromString(text + "\n"));

        Flow<ByteString, ByteString, NotUsed> serverFlow =
                Flow.fromSinkAndSource(sinkWithFraming, sourceWithFraming);

        Tcp.get(system)
                .bind("127.0.0.1", 9999)
                .runForeach(
                        incomingConnection -> incomingConnection.handleWith(serverFlow, system), system);
        // #chat
    }

    public static void main(String[] args) {
        chat();
    }
}
