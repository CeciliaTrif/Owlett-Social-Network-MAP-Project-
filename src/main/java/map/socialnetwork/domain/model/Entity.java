package map.socialnetwork.domain.model;

import java.io.Serializable;

public abstract class Entity<ID> implements Serializable {

    private static final Long serialVersionUID = 1L;

    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}