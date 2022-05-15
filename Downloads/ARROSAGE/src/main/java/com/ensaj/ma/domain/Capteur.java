package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Capteur.
 */
@Entity
@Table(name = "capteur")
public class Capteur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ref")
    private String ref;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @OneToMany(mappedBy = "capteur")
    @JsonIgnoreProperties(value = { "boitier", "capteur" }, allowSetters = true)
    private Set<Connecte> connectes = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "capteurs", "installations", "connectes" }, allowSetters = true)
    private Boitier boitier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Capteur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRef() {
        return this.ref;
    }

    public Capteur ref(String ref) {
        this.setRef(ref);
        return this;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return this.type;
    }

    public Capteur type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Capteur photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Capteur photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Set<Connecte> getConnectes() {
        return this.connectes;
    }

    public void setConnectes(Set<Connecte> connectes) {
        if (this.connectes != null) {
            this.connectes.forEach(i -> i.setCapteur(null));
        }
        if (connectes != null) {
            connectes.forEach(i -> i.setCapteur(this));
        }
        this.connectes = connectes;
    }

    public Capteur connectes(Set<Connecte> connectes) {
        this.setConnectes(connectes);
        return this;
    }

    public Capteur addConnecte(Connecte connecte) {
        this.connectes.add(connecte);
        connecte.setCapteur(this);
        return this;
    }

    public Capteur removeConnecte(Connecte connecte) {
        this.connectes.remove(connecte);
        connecte.setCapteur(null);
        return this;
    }

    public Boitier getBoitier() {
        return this.boitier;
    }

    public void setBoitier(Boitier boitier) {
        this.boitier = boitier;
    }

    public Capteur boitier(Boitier boitier) {
        this.setBoitier(boitier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Capteur)) {
            return false;
        }
        return id != null && id.equals(((Capteur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Capteur{" +
            "id=" + getId() +
            ", ref='" + getRef() + "'" +
            ", type='" + getType() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            "}";
    }
}
