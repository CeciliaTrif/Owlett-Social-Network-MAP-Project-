package map.socialnetwork.domain.model;

import java.time.LocalDateTime;

public class Message extends Entity<Long> {

    private Long senderID;
    private Long receiverID;
    private LocalDateTime messageTime;
    private String message;
    private Long replyMessageID;

    public Message(Long senderID, Long receiverID, LocalDateTime messageTime, String message, Long getReplyMessageID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageTime = messageTime;
        this.message = message;
        this.replyMessageID = getReplyMessageID;
    }



    public Long getSenderID() {
        return senderID;
    }

    public Message setSenderID(Long senderID) {
        this.senderID = senderID;
        return this;
    }

    public Long getReceiverID() {
        return receiverID;
    }

    public Message setReceiverID(Long receiverID) {
        this.receiverID = receiverID;
        return this;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public Message setMessageTime(LocalDateTime messageTime) {
        this.messageTime = messageTime;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getReplyMessageID() {
        return replyMessageID;
    }

    public Message setReplyMessageID(Long replyMessageID) {
        this.replyMessageID = replyMessageID;
        return this;
    }

    @Override
    public String toString() {
        return "From: " + senderID +
                "\nTo: " + receiverID +
                "\nMessage: " + message +
                "\nAt: " + messageTime +
                "\n";
    }
}
