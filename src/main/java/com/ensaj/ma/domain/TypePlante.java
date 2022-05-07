package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A TypePlante.
 */
@Entity
@Table(name = "type_plante")
public class TypePlante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "humidite_max")
    private Float humiditeMax;

    @Column(name = "humidite_min")
    private Float humiditeMin;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "luminosite")
    private Float luminosite;

    @OneToMany(mappedBy = "typePlante")
    @JsonIgnoreProperties(value = { "plantages", "typePlante" }, allowSetters = true)
    private Set<Plante> plantes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TypePlante id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public TypePlante libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Float getHumiditeMax() {
        return this.humiditeMax;
    }

    public TypePlante humiditeMax(Float humiditeMax) {
        this.setHumiditeMax(humiditeMax);
        return this;
    }

    public void setHumiditeMax(Float humiditeMax) {
        this.humiditeMax = humiditeMax;
    }

    public Float getHumiditeMin() {
        return this.humiditeMin;
    }

    public TypePlante humiditeMin(Float humiditeMin) {
        this.setHumiditeMin(humiditeMin);
        return this;
    }

    public void setHumiditeMin(Float humiditeMin) {
        this.humiditeMin = humiditeMin;
    }

    public Float getTemperature() {
        return this.temperature;
    }

    public TypePlante temperature(Float temperature) {
        this.setTemperature(temperature);
        return this;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getLuminosite() {
        return this.luminosite;
    }

    public TypePlante luminosite(Float luminosite) {
        this.setLuminosite(luminosite);
        return this;
    }

    public void setLuminosite(Float luminosite) {
        this.luminosite = luminosite;
    }

    public Set<Plante> getPlantes() {
        return this.plantes;
    }

    public void setPlantes(Set<Plante> plantes) {
        if (this.plantes != null) {
            this.plantes.forEach(i -> i.setTypePlante(null));
        }
        if (plantes != null) {
            plantes.forEach(i -> i.setTypePlante(this));
        }
        this.plantes = plantes;
    }

    public TypePlante plantes(Set<Plante> plantes) {
        this.setPlantes(plantes);
        return this;
    }

    public TypePlante addPlante(Plante plante) {
        this.plantes.add(plante);
        plante.setTypePlante(this);
        return this;
    }

    public TypePlante removePlante(Plante plante) {
        this.plantes.remove(plante);
        plante.setTypePlante(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypePlante)) {
            return false;
        }
        return id != null && id.equals(((TypePlante) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypePlante{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", humiditeMax=" + getHumiditeMax() +
            ", humiditeMin=" + getHumiditeMin() +
            ", temperature=" + getTemperature() +
            ", luminosite=" + getLuminosite() +
            "}";
    }
}
