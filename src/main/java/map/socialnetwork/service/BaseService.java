package map.socialnetwork.service;

import map.socialnetwork.domain.model.Entity;
import map.socialnetwork.repository.Repository;

import java.io.Serializable;
import java.util.Collection;

public abstract class BaseService<ENTITY extends Entity<ID>, ID extends Serializable> {

    private final Repository<ENTITY, ID> repository;

    public BaseService(Repository<ENTITY, ID> repository) {
        this.repository = repository;
    }

    public void add(ENTITY entity) {
        getRepository().save(entity);
    }

    public void delete(ID id) {
        getRepository().delete(id);
    }

    public void update(ENTITY entity) {
        getRepository().update(entity);
    }

    public Collection<ENTITY> getAll() {
        return getRepository().getAll();
    }

    public Iterable<ENTITY> findAll() {
        return getRepository().findAll();
    }

    public ENTITY findById(ID id) {
        return getRepository().findOne(id);
    }

    public Repository<ENTITY, ID> getRepository() {
        return repository;
    }
}
