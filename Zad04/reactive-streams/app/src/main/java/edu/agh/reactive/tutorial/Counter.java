package edu.agh.reactive.tutorial;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Receive;

public class Counter extends AbstractBehavior<Counter.Command> {

    public interface Command {}

    public enum Increment implements Command {
        INSTANCE
    }

    public static class GetValue implements Command {
        public final ActorRef<Value> replyTo;

        public GetValue(ActorRef<Value> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class Value {
        public final int value;

        public Value(int value) {
            this.value = value;
        }
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(Counter::new);
    }

    private int n;

    private Counter(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Increment.class, notUsed -> onIncrement())
                .onMessage(GetValue.class, this::onGetValue)
                .build();
    }

    private Behavior<Command> onIncrement() {
        n++;
        getContext().getLog().debug("Incremented counter to [{}]", n);
        return this;
    }

    private Behavior<Command> onGetValue(GetValue command) {
        command.replyTo.tell(new Value(n));
        return this;
    }
}