package com.ensaj.ma.repository;

import com.ensaj.ma.domain.Boitier;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Boitier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoitierRepository extends JpaRepository<Boitier, Long> {}
