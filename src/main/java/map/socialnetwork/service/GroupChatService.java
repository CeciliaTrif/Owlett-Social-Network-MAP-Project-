package map.socialnetwork.service;

import map.socialnetwork.domain.model.GroupChat;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.repository.database.GroupChatDatabaseRepository;
import map.socialnetwork.util.Container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<GroupChat> getAllByUserId(Long userId) {
        return getGroupChatDatabaseRepository().getAllByUserId(userId);
    }

    public Map<Long, User> getAllGroupChatParticipants(Long id) {
        List<Long> participantsIds = getGroupChatDatabaseRepository().getAllParticipantIds(id);

        Map<Long, User> participants = new HashMap<>();
        UserService userService = (UserService) Container.getService("user");
        for(Long participantId : participantsIds) {
            assert userService != null;
            User user = userService.findById(participantId);
            if(user != null) {
                participants.put(participantId, user);
            }
        }
        return participants;
    }

    private GroupChatDatabaseRepository getGroupChatDatabaseRepository() {
        return (GroupChatDatabaseRepository) getRepository();
    }
}
