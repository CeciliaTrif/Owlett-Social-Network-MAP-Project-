package map.socialnetwork.domain.validator;

import map.socialnetwork.domain.model.User;

public class UserValidator implements Validator<User> {

    private final EntityValidator entityValidator = EntityValidator.getSingletonInstance();

    /**
     * This method is used to validate a User class object.
     * In case the user's Id is null that means the entity does not exist.
     * In case the name is null or is an empty string or in case only white spaces are used in it, we trim and see
     * if it's still empty. If any of the cases above is true then we trow a validation exception.
     *
     * @param user The User Class's entity that needs to be validated.
     * @throws ValidationException - specific validation exception in case the entity has missing information.
     */
    @Override
    public void validate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User object is null.");
        }
        entityValidator.validate(user);
        if (user.getId() == null) {
            throw new ValidationException("Null ID.");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUsername().trim().isEmpty()) {
            throw new ValidationException("Null name.");
        }
    }
}
