package map.socialnetwork.domain.validator;

import map.socialnetwork.domain.model.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    private final EntityValidator entityValidator = EntityValidator.getSingletonInstance();

    public FriendshipValidator() {
    }

    /**
     * This method is used to validate a Friendship class object.
     * We need to verify that both user ids are populated and they must not be identical.
     *
     * @param friendship The Friendship Class' entity that needs to be validated.
     * @throws ValidationException - specific validation exception in case the entity has missing information.
     * */
    public void validate(Friendship friendship) throws ValidationException {
        if(friendship == null) {
            throw new ValidationException("Friendship object is null.");
        }
        entityValidator.validate(friendship);
        if(friendship.getUserID1() == null) {
            throw new ValidationException("The user1 id cannot be null!");
        }

        if(friendship.getUserID2() == null) {
            throw new ValidationException("The user2 id cannot be null!");
        }

        if(friendship.getUserID2().equals(friendship.getUserID1())) {
            throw new ValidationException("A user cannot send a friend request to himself");
        }
    }
}
