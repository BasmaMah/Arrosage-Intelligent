package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Parcelle.
 */
@Entity
@Table(name = "parcelle")
public class Parcelle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "surface")
    private Float surface;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Column(name = "libelle")
    private String libelle;

    @OneToMany(mappedBy = "parcelle")
    @JsonIgnoreProperties(value = { "parcelle", "boitier" }, allowSetters = true)
    private Set<Installation> installations = new HashSet<>();

    @OneToMany(mappedBy = "parcelle")
    @JsonIgnoreProperties(value = { "parcelle", "plante" }, allowSetters = true)
    private Set<Plantage> plantages = new HashSet<>();

    @OneToMany(mappedBy = "parcelle")
    @JsonIgnoreProperties(value = { "parcelle" }, allowSetters = true)
    private Set<Arrosage> arrosages = new HashSet<>();

    @OneToMany(mappedBy = "parcelle")
    @JsonIgnoreProperties(value = { "parcelle" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "parcelle")
    @JsonIgnoreProperties(value = { "parcelle" }, allowSetters = true)
    private Set<Grandeur> grandeurs = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "parcelles" }, allowSetters = true)
    private TypeSol typeSol;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parcelles", "extraUser" }, allowSetters = true)
    private Ferme ferme;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Parcelle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getSurface() {
        return this.surface;
    }

    public Parcelle surface(Float surface) {
        this.setSurface(surface);
        return this;
    }

    public void setSurface(Float surface) {
        this.surface = surface;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Parcelle photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Parcelle photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Parcelle libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Set<Installation> getInstallations() {
        return this.installations;
    }

    public void setInstallations(Set<Installation> installations) {
        if (this.installations != null) {
            this.installations.forEach(i -> i.setParcelle(null));
        }
        if (installations != null) {
            installations.forEach(i -> i.setParcelle(this));
        }
        this.installations = installations;
    }

    public Parcelle installations(Set<Installation> installations) {
        this.setInstallations(installations);
        return this;
    }

    public Parcelle addInstallation(Installation installation) {
        this.installations.add(installation);
        installation.setParcelle(this);
        return this;
    }

    public Parcelle removeInstallation(Installation installation) {
        this.installations.remove(installation);
        installation.setParcelle(null);
        return this;
    }

    public Set<Plantage> getPlantages() {
        return this.plantages;
    }

    public void setPlantages(Set<Plantage> plantages) {
        if (this.plantages != null) {
            this.plantages.forEach(i -> i.setParcelle(null));
        }
        if (plantages != null) {
            plantages.forEach(i -> i.setParcelle(this));
        }
        this.plantages = plantages;
    }

    public Parcelle plantages(Set<Plantage> plantages) {
        this.setPlantages(plantages);
        return this;
    }

    public Parcelle addPlantage(Plantage plantage) {
        this.plantages.add(plantage);
        plantage.setParcelle(this);
        return this;
    }

    public Parcelle removePlantage(Plantage plantage) {
        this.plantages.remove(plantage);
        plantage.setParcelle(null);
        return this;
    }

    public Set<Arrosage> getArrosages() {
        return this.arrosages;
    }

    public void setArrosages(Set<Arrosage> arrosages) {
        if (this.arrosages != null) {
            this.arrosages.forEach(i -> i.setParcelle(null));
        }
        if (arrosages != null) {
            arrosages.forEach(i -> i.setParcelle(this));
        }
        this.arrosages = arrosages;
    }

    public Parcelle arrosages(Set<Arrosage> arrosages) {
        this.setArrosages(arrosages);
        return this;
    }

    public Parcelle addArrosage(Arrosage arrosage) {
        this.arrosages.add(arrosage);
        arrosage.setParcelle(this);
        return this;
    }

    public Parcelle removeArrosage(Arrosage arrosage) {
        this.arrosages.remove(arrosage);
        arrosage.setParcelle(null);
        return this;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setParcelle(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setParcelle(this));
        }
        this.notifications = notifications;
    }

    public Parcelle notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public Parcelle addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setParcelle(this);
        return this;
    }

    public Parcelle removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setParcelle(null);
        return this;
    }

    public Set<Grandeur> getGrandeurs() {
        return this.grandeurs;
    }

    public void setGrandeurs(Set<Grandeur> grandeurs) {
        if (this.grandeurs != null) {
            this.grandeurs.forEach(i -> i.setParcelle(null));
        }
        if (grandeurs != null) {
            grandeurs.forEach(i -> i.setParcelle(this));
        }
        this.grandeurs = grandeurs;
    }

    public Parcelle grandeurs(Set<Grandeur> grandeurs) {
        this.setGrandeurs(grandeurs);
        return this;
    }

    public Parcelle addGrandeur(Grandeur grandeur) {
        this.grandeurs.add(grandeur);
        grandeur.setParcelle(this);
        return this;
    }

    public Parcelle removeGrandeur(Grandeur grandeur) {
        this.grandeurs.remove(grandeur);
        grandeur.setParcelle(null);
        return this;
    }

    public TypeSol getTypeSol() {
        return this.typeSol;
    }

    public void setTypeSol(TypeSol typeSol) {
        this.typeSol = typeSol;
    }

    public Parcelle typeSol(TypeSol typeSol) {
        this.setTypeSol(typeSol);
        return this;
    }

    public Ferme getFerme() {
        return this.ferme;
    }

    public void setFerme(Ferme ferme) {
        this.ferme = ferme;
    }

    public Parcelle ferme(Ferme ferme) {
        this.setFerme(ferme);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parcelle)) {
            return false;
        }
        return id != null && id.equals(((Parcelle) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parcelle{" +
            "id=" + getId() +
            ", surface=" + getSurface() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
