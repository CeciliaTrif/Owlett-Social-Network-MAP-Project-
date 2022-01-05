package map.socialnetwork.service;

import map.socialnetwork.domain.model.Message;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.repository.database.MessageDatabaseRepository;

import java.util.Collection;
import java.util.List;

public class MessageService extends BaseService<Message, Long> {

    public MessageService(Repository<Message, Long> repository) {
        super(repository);
    }

    public List<Message> getMessagesById(Long sender_id, Long receiver_id) {
        return getMessages().stream()
                .filter(message -> message.getSenderID().equals(sender_id) && message.getReceiverID().equals(receiver_id)
                        || message.getSenderID().equals(receiver_id) && message.getReceiverID().equals(sender_id))
                .toList();
    }

    private Collection<Message> getMessages() {
        return ((MessageDatabaseRepository)getRepository()).getMessages();
    }

    public void sendMessage(Message message) {
        add(message);
    }



}
