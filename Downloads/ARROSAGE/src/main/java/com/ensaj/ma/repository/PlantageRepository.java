package com.ensaj.ma.repository;

import com.ensaj.ma.domain.Plantage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Plantage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlantageRepository extends JpaRepository<Plantage, Long> {}
