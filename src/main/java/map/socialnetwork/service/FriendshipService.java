package map.socialnetwork.service;

import map.socialnetwork.domain.model.Friendship;
import map.socialnetwork.domain.validator.ValidationException;
import map.socialnetwork.repository.Repository;
import map.socialnetwork.repository.database.FriendshipDatabaseRepository;

import java.util.Collection;
import java.util.List;

public class FriendshipService extends map.socialnetwork.service.BaseService<Friendship, Long> {


    public FriendshipService(Repository<Friendship, Long> friendshipRepository) {
        super(friendshipRepository);
    }

    public void addToRepo(Friendship friendship){
        if (getRequestFromTo(friendship.getUserID1(), friendship.getUserID2()).isEmpty() &&
                getRequestFromTo(friendship.getUserID2(), friendship.getUserID1()).isEmpty()) {
            add(friendship);
        } else {
            throw new ValidationException("A relation between the users already exists!");
        }
    }

    public List<Friendship> getRequestFromTo(Long user_1, Long user_2) {
        return getRequests().stream()
                .filter(friendship -> friendship.getUserID1().equals(user_1) && friendship.getUserID2().equals(user_2)).toList();
    }

    public List<Friendship> getRequestsFrom(Long user_1) {
        return getRequests().stream()
                .filter(friendship -> friendship.getUserID1().equals(user_1)).toList();
    }


    public List<Friendship> getRequestsByID(Long user_id) {
        return getRequests().stream()
                .filter(friendship -> friendship.getUserID2().equals(user_id))
                .toList();
    }

    public Friendship delete(Friendship f) {
        Friendship friendship = getRepository().delete(f.getId());
        return null;
    }

    public List<Friendship> getAllByUserId(Long userId) {
        return getFriendshipRepository().getAllByUserId(userId);
    }

    private Collection<Friendship> getRequests() {
        return ((FriendshipDatabaseRepository)getRepository()).getRequests();
    }

    private FriendshipDatabaseRepository getFriendshipRepository() {
        return (FriendshipDatabaseRepository) getRepository();
    }

}
