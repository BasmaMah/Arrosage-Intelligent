package com.ensaj.ma.repository;

import com.ensaj.ma.domain.Ferme;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Ferme entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FermeRepository extends JpaRepository<Ferme, Long> {}

