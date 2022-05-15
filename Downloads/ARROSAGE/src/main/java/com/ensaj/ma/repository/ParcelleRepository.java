package com.ensaj.ma.repository;

import com.ensaj.ma.domain.Ferme;
import com.ensaj.ma.domain.Parcelle;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Parcelle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParcelleRepository extends JpaRepository<Parcelle, Long> {
      public List<Parcelle>  findParcelleByFerme (Optional<Ferme> ferme) ;

}
