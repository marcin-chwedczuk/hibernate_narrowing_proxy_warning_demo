package com.example.hibernateproxydemo.model.legacysystem;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id")
    private LegacyDocument document;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private ExtendedUser author;

    @Column
    private String contents;

    public Comment() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LegacyDocument getDocument() {
        return document;
    }

    public void setDocument(LegacyDocument document) {
        this.document = document;
    }

    public ExtendedUser getAuthor() {
        return author;
    }

    public void setAuthor(ExtendedUser author) {
        this.author = author;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
