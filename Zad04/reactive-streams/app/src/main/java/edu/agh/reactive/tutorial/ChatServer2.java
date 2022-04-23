package edu.agh.reactive.tutorial;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.japi.pf.PFBuilder;
import akka.stream.javadsl.*;
import akka.util.ByteString;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

public class ChatServer2 extends AllDirectives {
    Flow<Message, Message, NotUsed> serverFlow;
    Sink<Message, NotUsed> sink;
    Source<Message, NotUsed> source;
    ActorSystem system;

    public ChatServer2() {
        system = ActorSystem.create("routes");
        System.out.println("CONSTRUCTOR");
    }

    void run() throws IOException {
        System.out.println(this);
        // boot up server using the route as defined below

        final Http http = Http.get(system);

        //In order to access all directives we need an instance where the routes are define.
        ChatServer2 app = new ChatServer2();


        final CompletionStage<ServerBinding> binding =
                http.newServerAt("localhost", 12345)
                        .bind(app.concat(app.defaultRoute(), app.affirmRoute()));

        System.out.println("Server online at http://localhost:12345/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }

    private Route defaultRoute() {
        return pathEndOrSingleSlash(() ->
                get(() ->
                        complete("Welcome to chat")));
    }

    Iterable<Message> duplicate(Message i) {
        return Arrays.asList(i, i);
    }

    private Route affirmRoute() {
        Pair<Sink<Message, NotUsed>, Source<Message, NotUsed>> pair =
                MergeHub.of(Message.class).toMat(BroadcastHub.of(Message.class), Keep.both()).run(system);
        sink = pair.first();
        source = pair.second();
        serverFlow = Flow.fromSinkAndSource(sink, source);
        return path("affirm", () -> handleWebSocketMessages(serverFlow));
    }

    public static void main(String[] args) throws IOException {
        ChatServer2 server = new ChatServer2();
        server.run();
    }
}
