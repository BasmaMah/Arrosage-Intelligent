package com.ensaj.ma.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A ExtraUser.
 */
@Entity
@Table(name = "extra_user")
public class ExtraUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "extraUser")
    @JsonIgnoreProperties(value = { "parcelles", "extraUser" }, allowSetters = true)
    private Set<Ferme> fermes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExtraUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public ExtraUser phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public ExtraUser address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ExtraUser user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Ferme> getFermes() {
        return this.fermes;
    }

    public void setFermes(Set<Ferme> fermes) {
        if (this.fermes != null) {
            this.fermes.forEach(i -> i.setExtraUser(null));
        }
        if (fermes != null) {
            fermes.forEach(i -> i.setExtraUser(this));
        }
        this.fermes = fermes;
    }

    public ExtraUser fermes(Set<Ferme> fermes) {
        this.setFermes(fermes);
        return this;
    }

    public ExtraUser addFerme(Ferme ferme) {
        this.fermes.add(ferme);
        ferme.setExtraUser(this);
        return this;
    }

    public ExtraUser removeFerme(Ferme ferme) {
        this.fermes.remove(ferme);
        ferme.setExtraUser(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtraUser)) {
            return false;
        }
        return id != null && id.equals(((ExtraUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExtraUser{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
