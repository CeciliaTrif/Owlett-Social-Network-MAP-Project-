package map.socialnetwork.domain.validator;

public interface Validator<ENTITY> {

    void validate(ENTITY entity) throws ValidationException;
}
