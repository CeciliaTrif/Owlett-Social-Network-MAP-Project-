package map.socialnetwork.service;

import map.socialnetwork.domain.model.Friendship;
import map.socialnetwork.domain.model.User;
import map.socialnetwork.repository.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class UserService extends map.socialnetwork.service.BaseService<User, Long> {

    private final map.socialnetwork.service.FriendshipService friendshipService;

    public UserService(Repository<User, Long> repository, map.socialnetwork.service.FriendshipService friendshipService) {
        super(repository);
        this.friendshipService = friendshipService;
    }

    @Override
    public void delete(Long id) {
        findById(id);
        List<Friendship> friendshipsToBeDeleted = friendshipService.getAll()
                .stream()
                .filter(friendship -> friendship.getUserID1().equals(id) || friendship.getUserID2().equals(id)).toList();
        for (Friendship friendship : friendshipsToBeDeleted) {
            friendshipService.delete(friendship.getId());
        }
        super.delete(id);
    }

    public List<Friendship> getUserFriendshipsNames(Long id) {
        return friendshipService.getAll()
                .stream()
                .filter(friendship -> friendship.getUserID1().equals(id) || friendship.getUserID2().equals(id))
                .collect(Collectors.toList());
    }

    public List<Friendship> getUserFriendshipsInAMonth(Long id, int month) {
        return friendshipService.getAll()
                .stream()
                .filter(friendship -> {
                    if (friendship.getUserID1().equals(id) || friendship.getUserID2().equals(id)) {
                        long dateInMillis = friendship.getFriendshipStartDate().getTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(dateInMillis);
                        return calendar.get(Calendar.MONTH) + 1 == month;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
