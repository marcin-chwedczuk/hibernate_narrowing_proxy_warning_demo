package com.example.hibernateproxydemo.model.legacysystem;

import javax.persistence.*;

@Entity
@Table(name = "document")
public class LegacyDocument {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String contents;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private LegacyUser owner;

    public LegacyDocument() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public LegacyUser getOwner() {
        return owner;
    }

    public void setOwner(LegacyUser owner) {
        this.owner = owner;
    }
}
