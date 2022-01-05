package map.socialnetwork.domain.validator;


import map.socialnetwork.domain.model.Entity;

public class EntityValidator implements Validator<Entity>{

    private static final EntityValidator singletonInstance = new EntityValidator();
    @Override
    public void validate(Entity entity) throws ValidationException {
        if (entity.getId() == null) {
            throw new ValidationException("Entity ID can't be null.");
        }
    }

    public static EntityValidator getSingletonInstance() {
        return singletonInstance;
    }
}
