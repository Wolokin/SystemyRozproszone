package edu.agh.reactive.chat;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.stream.javadsl.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.segment;
import static java.util.regex.Pattern.compile;

public class ChatServer extends AllDirectives {
    Flow<Message, Message, NotUsed> serverFlow;
    Sink<Message, NotUsed> sink;
    Source<Message, NotUsed> source;
    ActorSystem system;

    public ChatServer() {
        Config customConf = ConfigFactory.parseString("akka.http.server.idle-timeout = infinite");
        system = ActorSystem.create("chat", ConfigFactory.load(customConf));
    }

    void run() throws IOException {
        System.out.println(this);

        final Http http = Http.get(system);
        ChatServer server = new ChatServer();


        final CompletionStage<ServerBinding> binding =
                http.newServerAt("localhost", 12345)
                        .bind(server.concat(server.defaultRoute(), server.messageRoute()));

        System.out.println("Server online at http://localhost:12345/\nPress ENTER to stop...");
        System.in.read();

        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route defaultRoute() {
        System.out.println(System.getProperty("user.dir"));
        return pathEndOrSingleSlash(() ->
                get(() ->
                        getFromResource("client.html")));
    }

    private Route messageRoute() {
        Pair<Sink<Message, NotUsed>, Source<Message, NotUsed>> pair =
                MergeHub.of(Message.class).toMat(BroadcastHub.of(Message.class), Keep.both()).run(system);
        sink = pair.first();
        source = pair.second();
        serverFlow = Flow.fromSinkAndSource(sink, source);
        return path(segment("affirm").slash(segment(compile("..*"))), name -> handleWebSocketMessages(
                Flow.of(Message.class)
                        .map(msg -> (Message) TextMessage.create(name + ": " + msg.asTextMessage().getStrictText()))
                        .via(serverFlow)));
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.run();
    }
}
