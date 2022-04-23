package edu.agh.reactive.tutorial;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.pf.PFBuilder;
import akka.stream.DelayOverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.Int;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChatServer extends AllDirectives {
    static void run() throws IOException {
        // boot up server using the route as defined below
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);

        //In order to access all directives we need an instance where the routes are define.
        ChatServer app = new ChatServer();

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
        Source<Integer, NotUsed> source = Source.from(IntStream.range(1, 501).boxed().toList()).throttle(4, Duration.ofSeconds(5));
//                .toMat(Sink.ignore());

        return path("affirm", () -> handleWebSocketMessages(
//                Flow.of(Message.class).fromSinkAndSource(Sink.ignore(), source
                Flow.of(Message.class)
                        .collect(new PFBuilder<Message, Message>()
                        .match(TextMessage.class, s -> TextMessage.create("You said " + s.getStrictText())).build())
//                .mapConcat(this::duplicate)
                .merge(source.map(i -> TextMessage.create(i.toString())))));
//        ).map(s -> TextMessage.create(s))));
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}
