package edu.agh.reactive.tutorial;

import akka.actor.ActorSystem;
import edu.agh.reactive.chat.Chat;

import java.util.HashMap;
import java.util.Map;

public class ChatSessionMap {
    private Map<String, ChatSession> map = new HashMap<>();
    private ActorSystem system;

    public ChatSessionMap(ActorSystem system) {
        this.system = system;
    }

    ChatSession getOrAdd(String s) {
        return map.containsKey(s) ? map.get(s) : create(s);
    }

    private ChatSession create(String userId) {
        ChatSession session = new ChatSession(userId, system);
        map.put(userId, session);
        return session;
    }
}
