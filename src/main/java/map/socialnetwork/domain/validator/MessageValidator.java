package map.socialnetwork.domain.validator;

import map.socialnetwork.domain.model.Message;

public class MessageValidator implements Validator<Message> {

    private final EntityValidator entityValidator = EntityValidator.getSingletonInstance();

    /**
     * This method is used to validate a Message class object.
     * We need to verify that both user ids are populated, and they must not be identical.
     *
     * @param message The Message Class' entity that needs to be validated.
     * @throws ValidationException - specific validation exception in case the entity has missing information.
     */
    public void validate(Message message) throws ValidationException {
        if (message == null) {
            throw new ValidationException("Message object is null!");
        }

        if (message.getSenderID() == null) {
            throw new ValidationException("The sender id cannot be null!");
        }

        if (message.getReceiverID() == null) {
            throw new ValidationException("The receiver id cannot be null!");
        }

        if (message.getReceiverID().equals(message.getSenderID())) {
            throw new ValidationException("A message cannot happen with the same user!");
        }

        if (message.getMessage().isEmpty()) {
            throw new ValidationException("Message can not be empty!");
        }

    }
}
