package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Boitier.
 */
@Entity
@Table(name = "boitier")
public class Boitier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "ref", nullable = false)
    private Integer ref;

    @Column(name = "type")
    private String type;

    @Column(name = "nbr_branch_boitier")
    private Integer nbrBranchBoitier;

    @Column(name = "nbr_branch_arduino")
    private Integer nbrBranchArduino;

    @Column(name = "code")
    private String code;

    @OneToMany(mappedBy = "boitier")
    @JsonIgnoreProperties(value = { "connectes", "boitier" }, allowSetters = true)
    private Set<Capteur> capteurs = new HashSet<>();

    @OneToMany(mappedBy = "boitier")
    @JsonIgnoreProperties(value = { "parcelle", "boitier" }, allowSetters = true)
    private Set<Installation> installations = new HashSet<>();

    @OneToMany(mappedBy = "boitier")
    @JsonIgnoreProperties(value = { "boitier", "capteur" }, allowSetters = true)
    private Set<Connecte> connectes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Boitier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRef() {
        return this.ref;
    }

    public Boitier ref(Integer ref) {
        this.setRef(ref);
        return this;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public String getType() {
        return this.type;
    }

    public Boitier type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNbrBranchBoitier() {
        return this.nbrBranchBoitier;
    }

    public Boitier nbrBranchBoitier(Integer nbrBranchBoitier) {
        this.setNbrBranchBoitier(nbrBranchBoitier);
        return this;
    }

    public void setNbrBranchBoitier(Integer nbrBranchBoitier) {
        this.nbrBranchBoitier = nbrBranchBoitier;
    }

    public Integer getNbrBranchArduino() {
        return this.nbrBranchArduino;
    }

    public Boitier nbrBranchArduino(Integer nbrBranchArduino) {
        this.setNbrBranchArduino(nbrBranchArduino);
        return this;
    }

    public void setNbrBranchArduino(Integer nbrBranchArduino) {
        this.nbrBranchArduino = nbrBranchArduino;
    }

    public String getCode() {
        return this.code;
    }

    public Boitier code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Capteur> getCapteurs() {
        return this.capteurs;
    }

    public void setCapteurs(Set<Capteur> capteurs) {
        if (this.capteurs != null) {
            this.capteurs.forEach(i -> i.setBoitier(null));
        }
        if (capteurs != null) {
            capteurs.forEach(i -> i.setBoitier(this));
        }
        this.capteurs = capteurs;
    }

    public Boitier capteurs(Set<Capteur> capteurs) {
        this.setCapteurs(capteurs);
        return this;
    }

    public Boitier addCapteur(Capteur capteur) {
        this.capteurs.add(capteur);
        capteur.setBoitier(this);
        return this;
    }

    public Boitier removeCapteur(Capteur capteur) {
        this.capteurs.remove(capteur);
        capteur.setBoitier(null);
        return this;
    }

    public Set<Installation> getInstallations() {
        return this.installations;
    }

    public void setInstallations(Set<Installation> installations) {
        if (this.installations != null) {
            this.installations.forEach(i -> i.setBoitier(null));
        }
        if (installations != null) {
            installations.forEach(i -> i.setBoitier(this));
        }
        this.installations = installations;
    }

    public Boitier installations(Set<Installation> installations) {
        this.setInstallations(installations);
        return this;
    }

    public Boitier addInstallation(Installation installation) {
        this.installations.add(installation);
        installation.setBoitier(this);
        return this;
    }

    public Boitier removeInstallation(Installation installation) {
        this.installations.remove(installation);
        installation.setBoitier(null);
        return this;
    }

    public Set<Connecte> getConnectes() {
        return this.connectes;
    }

    public void setConnectes(Set<Connecte> connectes) {
        if (this.connectes != null) {
            this.connectes.forEach(i -> i.setBoitier(null));
        }
        if (connectes != null) {
            connectes.forEach(i -> i.setBoitier(this));
        }
        this.connectes = connectes;
    }

    public Boitier connectes(Set<Connecte> connectes) {
        this.setConnectes(connectes);
        return this;
    }

    public Boitier addConnecte(Connecte connecte) {
        this.connectes.add(connecte);
        connecte.setBoitier(this);
        return this;
    }

    public Boitier removeConnecte(Connecte connecte) {
        this.connectes.remove(connecte);
        connecte.setBoitier(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Boitier)) {
            return false;
        }
        return id != null && id.equals(((Boitier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Boitier{" +
            "id=" + getId() +
            ", ref=" + getRef() +
            ", type='" + getType() + "'" +
            ", nbrBranchBoitier=" + getNbrBranchBoitier() +
            ", nbrBranchArduino=" + getNbrBranchArduino() +
            ", code='" + getCode() + "'" +
            "}";
    }
}
