package map.socialnetwork.repository;

import map.socialnetwork.domain.model.Entity;
import map.socialnetwork.domain.validator.ValidationException;

import java.io.Serializable;
import java.util.Collection;

public interface Repository<ENTITY extends Entity<ID>, ID extends Serializable> {
    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    ENTITY findOne(ID id);

    /**
     * @return all entities
     */
    Iterable<ENTITY> findAll();

    Collection<ENTITY> getAll();

    Long generateNextID();

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    ENTITY save(ENTITY entity);


    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null.
     */
    ENTITY delete(ID id);

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidationException      if the entity is not valid.
     */
    ENTITY update(ENTITY entity);

    int getSize();
}
