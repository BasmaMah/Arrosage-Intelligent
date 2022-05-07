package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Plante.
 */
@Entity
@Table(name = "plante")
public class Plante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Column(name = "racine")
    private String racine;

    @OneToMany(mappedBy = "plante")
    @JsonIgnoreProperties(value = { "parcelle", "plante" }, allowSetters = true)
    private Set<Plantage> plantages = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "plantes" }, allowSetters = true)
    private TypePlante typePlante;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plante id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Plante libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Plante photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Plante photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getRacine() {
        return this.racine;
    }

    public Plante racine(String racine) {
        this.setRacine(racine);
        return this;
    }

    public void setRacine(String racine) {
        this.racine = racine;
    }

    public Set<Plantage> getPlantages() {
        return this.plantages;
    }

    public void setPlantages(Set<Plantage> plantages) {
        if (this.plantages != null) {
            this.plantages.forEach(i -> i.setPlante(null));
        }
        if (plantages != null) {
            plantages.forEach(i -> i.setPlante(this));
        }
        this.plantages = plantages;
    }

    public Plante plantages(Set<Plantage> plantages) {
        this.setPlantages(plantages);
        return this;
    }

    public Plante addPlantage(Plantage plantage) {
        this.plantages.add(plantage);
        plantage.setPlante(this);
        return this;
    }

    public Plante removePlantage(Plantage plantage) {
        this.plantages.remove(plantage);
        plantage.setPlante(null);
        return this;
    }

    public TypePlante getTypePlante() {
        return this.typePlante;
    }

    public void setTypePlante(TypePlante typePlante) {
        this.typePlante = typePlante;
    }

    public Plante typePlante(TypePlante typePlante) {
        this.setTypePlante(typePlante);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plante)) {
            return false;
        }
        return id != null && id.equals(((Plante) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plante{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", racine='" + getRacine() + "'" +
            "}";
    }
}
