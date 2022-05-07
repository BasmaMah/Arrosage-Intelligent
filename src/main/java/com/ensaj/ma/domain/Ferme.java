package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Ferme.
 */
@Entity
@Table(name = "ferme")
public class Ferme implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lebelle")
    private String lebelle;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @OneToMany(mappedBy = "ferme")
    @JsonIgnoreProperties(
        value = { "installations", "plantages", "arrosages", "notifications", "grandeurs", "typeSol", "ferme" },
        allowSetters = true
    )
    private Set<Parcelle> parcelles = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "fermes" }, allowSetters = true)
    private ExtraUser extraUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ferme id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLebelle() {
        return this.lebelle;
    }

    public Ferme lebelle(String lebelle) {
        this.setLebelle(lebelle);
        return this;
    }

    public void setLebelle(String lebelle) {
        this.lebelle = lebelle;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Ferme photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Ferme photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Set<Parcelle> getParcelles() {
        return this.parcelles;
    }

    public void setParcelles(Set<Parcelle> parcelles) {
        if (this.parcelles != null) {
            this.parcelles.forEach(i -> i.setFerme(null));
        }
        if (parcelles != null) {
            parcelles.forEach(i -> i.setFerme(this));
        }
        this.parcelles = parcelles;
    }

    public Ferme parcelles(Set<Parcelle> parcelles) {
        this.setParcelles(parcelles);
        return this;
    }

    public Ferme addParcelle(Parcelle parcelle) {
        this.parcelles.add(parcelle);
        parcelle.setFerme(this);
        return this;
    }

    public Ferme removeParcelle(Parcelle parcelle) {
        this.parcelles.remove(parcelle);
        parcelle.setFerme(null);
        return this;
    }

    public ExtraUser getExtraUser() {
        return this.extraUser;
    }

    public void setExtraUser(ExtraUser extraUser) {
        this.extraUser = extraUser;
    }

    public Ferme extraUser(ExtraUser extraUser) {
        this.setExtraUser(extraUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ferme)) {
            return false;
        }
        return id != null && id.equals(((Ferme) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ferme{" +
            "id=" + getId() +
            ", lebelle='" + getLebelle() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            "}";
    }
}
