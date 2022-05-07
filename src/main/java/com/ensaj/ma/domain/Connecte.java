package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Connecte.
 */
@Entity
@Table(name = "connecte")
public class Connecte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fonctionnel")
    private Boolean fonctionnel;

    @Column(name = "branche")
    private String branche;

    @Column(name = "frequence")
    private Float frequence;

    @Column(name = "marge")
    private Float marge;

    @ManyToOne
    @JsonIgnoreProperties(value = { "capteurs", "installations", "connectes" }, allowSetters = true)
    private Boitier boitier;

    @ManyToOne
    @JsonIgnoreProperties(value = { "connectes", "boitier" }, allowSetters = true)
    private Capteur capteur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Connecte id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFonctionnel() {
        return this.fonctionnel;
    }

    public Connecte fonctionnel(Boolean fonctionnel) {
        this.setFonctionnel(fonctionnel);
        return this;
    }

    public void setFonctionnel(Boolean fonctionnel) {
        this.fonctionnel = fonctionnel;
    }

    public String getBranche() {
        return this.branche;
    }

    public Connecte branche(String branche) {
        this.setBranche(branche);
        return this;
    }

    public void setBranche(String branche) {
        this.branche = branche;
    }

    public Float getFrequence() {
        return this.frequence;
    }

    public Connecte frequence(Float frequence) {
        this.setFrequence(frequence);
        return this;
    }

    public void setFrequence(Float frequence) {
        this.frequence = frequence;
    }

    public Float getMarge() {
        return this.marge;
    }

    public Connecte marge(Float marge) {
        this.setMarge(marge);
        return this;
    }

    public void setMarge(Float marge) {
        this.marge = marge;
    }

    public Boitier getBoitier() {
        return this.boitier;
    }

    public void setBoitier(Boitier boitier) {
        this.boitier = boitier;
    }

    public Connecte boitier(Boitier boitier) {
        this.setBoitier(boitier);
        return this;
    }

    public Capteur getCapteur() {
        return this.capteur;
    }

    public void setCapteur(Capteur capteur) {
        this.capteur = capteur;
    }

    public Connecte capteur(Capteur capteur) {
        this.setCapteur(capteur);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Connecte)) {
            return false;
        }
        return id != null && id.equals(((Connecte) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Connecte{" +
            "id=" + getId() +
            ", fonctionnel='" + getFonctionnel() + "'" +
            ", branche='" + getBranche() + "'" +
            ", frequence=" + getFrequence() +
            ", marge=" + getMarge() +
            "}";
    }
}
