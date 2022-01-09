package map.socialnetwork.service;

import map.socialnetwork.domain.model.Message;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.repository.database.MessageDatabaseRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class MessageService extends BaseService<Message, Long> {

    public MessageService(Repository<Message, Long> repository) {
        super(repository);
    }

    public List<Message> getMessagesByParticipantIds(Long participantId1, Long participantId2) {
        return getMessages().stream()
                .filter(message -> message.getSenderID().equals(participantId1) && message.getReceiverID().equals(participantId2)
                        || message.getSenderID().equals(participantId2) && message.getReceiverID().equals(participantId1))
                .sorted(Comparator.comparing(Message::getMessageTime))
                .toList();
    }

    public Message getLastMessageByParticipantIds(Long participantId1, Long participantId2) {
        return getMessages().stream()
                .filter(message -> message.getSenderID().equals(participantId1) && message.getReceiverID().equals(participantId2)
                        || message.getSenderID().equals(participantId2) && message.getReceiverID().equals(participantId1)).max(Comparator.comparing(Message::getMessageTime)).orElse(null);
    }

    private Collection<Message> getMessages() {
        return ((MessageDatabaseRepository)getRepository()).getMessages();
    }

    public void sendMessage(Message message) {
        Message lastMessage = getLastMessageByParticipantIds(message.getSenderID(), message.getReceiverID());
        if(lastMessage != null) {
            add(message);
            message = getLastMessageByParticipantIds(message.getSenderID(), message.getReceiverID());
            lastMessage.setReplyMessageID(message.getId());
            update(lastMessage);
        } else {
            add(message);
        }
    }


    public List<Message> getMessagesByReceiverId(Long id) {
        return getMessages().stream()
                .filter(message -> message.getReceiverID().equals(id))
                .sorted(Comparator.comparing(Message::getMessageTime))
                .toList();
    }
}
