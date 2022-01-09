package map.socialnetwork.domain.model;

import java.util.List;

public class GroupChat extends Entity<Long>{
    private String name;
    private List<User> participants;
    private List<Message> messages;

    public GroupChat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public GroupChat setName(String name) {
        this.name = name;
        return this;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public GroupChat setParticipants(List<User> participants) {
        this.participants = participants;
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public GroupChat setMessages(List<Message> messages) {
        this.messages = messages;
        return this;
    }
}
