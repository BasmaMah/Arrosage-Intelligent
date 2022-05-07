package com.ensaj.ma.repository;

import com.ensaj.ma.domain.Grandeur;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Grandeur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GrandeurRepository extends JpaRepository<Grandeur, Long> {}
