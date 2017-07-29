package com.example.hibernateproxydemo.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Type(type="pg-uuid")
    @Column(/* for H2: columnDefinition = "BINARY(16)",*/ updatable = false, nullable = false)
    private UUID id;

    @Version
    private long version;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
    }

    protected BaseEntity(UUID id) {
        this.id = Objects.requireNonNull(id);
    }

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof BaseEntity)) return false;

        BaseEntity that = (BaseEntity) o;

        // I skipped testing that o and this have same type,
        // because UUID are universally unique and no two entities
        // can share the same id.
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
