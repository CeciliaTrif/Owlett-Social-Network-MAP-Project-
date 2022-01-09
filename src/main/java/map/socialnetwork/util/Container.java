package map.socialnetwork.util;

import map.socialnetwork.domain.model.Entity;
import map.socialnetwork.domain.validator.FriendshipValidator;
import map.socialnetwork.domain.validator.GroupChatValidator;
import map.socialnetwork.domain.validator.MessageValidator;
import map.socialnetwork.domain.validator.UserValidator;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.repository.database.FriendshipDatabaseRepository;
import map.socialnetwork.repository.database.GroupChatDatabaseRepository;
import map.socialnetwork.repository.database.MessageDatabaseRepository;
import map.socialnetwork.repository.database.UserDatabaseRepository;
import map.socialnetwork.service.*;

import java.io.Serializable;

public final class Container {
    private static final UserDatabaseRepository userDatabaseRepository = new UserDatabaseRepository(new UserValidator());
    private static final MessageDatabaseRepository messageDatabaseRepository = new MessageDatabaseRepository(new MessageValidator());
    private static final GroupChatDatabaseRepository groupChatDatabaseRepository = new GroupChatDatabaseRepository(new GroupChatValidator());
    private static final FriendshipDatabaseRepository friendshipDatabaseRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userDatabaseRepository);
    private static final FriendshipService friendshipService =  new FriendshipService(friendshipDatabaseRepository);
    private static final UserService userService = new UserService(userDatabaseRepository, friendshipService);
    private static final MessageService messageService = new MessageService(messageDatabaseRepository);
    private static final GroupChatService groupChatService = new GroupChatService(groupChatDatabaseRepository);
    private Container() {
    }

    public static BaseService<? extends Entity<? extends Serializable>, ? extends Serializable> getService(String name) {
        switch (name) {
            case "user" -> {
                return userService;
            }
            case "friendship" -> {
                return friendshipService;
            }
            case "message" -> {
                return messageService;
            }
            case "groupChat" -> {
                return groupChatService;
            }
            default -> {return null;}
        }
    }

    public static Repository<? extends Entity<? extends Serializable>, ? extends Serializable> getRepository(String name) {
        switch (name) {
            case "user" -> {
                return userDatabaseRepository;
            }
            case "friendship" -> {
                return friendshipDatabaseRepository;
            }
            case "message" -> {
                return messageDatabaseRepository;
            }
            case "groupChat" -> {
                return groupChatDatabaseRepository;
            }
            default -> {return null;}
        }
    }

}
