package edu.agh.reactive.tutorial;

public interface ChatEvent {
}


record UserMessage(String message) implements ChatEvent {
}


