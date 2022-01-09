package map.socialnetwork.service;

import map.socialnetwork.domain.model.GroupChat;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.repository.database.GroupChatDatabaseRepository;

import java.util.List;

public class GroupChatService extends BaseService<GroupChat, Long>{
    public GroupChatService(Repository<GroupChat, Long> repository) {
        super(repository);
    }

    public void createGroupChat(String groupName, List<Long> groupParticipantIds) {
        GroupChat groupChat = new GroupChat(groupName);
        groupChat = getRepository().save(groupChat);
        for(Long groupParticipantId : groupParticipantIds) {
            getGroupChatDatabaseRepository().createGroupChatParticipant(groupChat.getId(), groupParticipantId);
        }

    }

    private GroupChatDatabaseRepository getGroupChatDatabaseRepository() {
        return (GroupChatDatabaseRepository) getRepository();
    }
}
