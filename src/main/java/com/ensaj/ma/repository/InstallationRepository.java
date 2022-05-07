package com.ensaj.ma.repository;

import com.ensaj.ma.domain.Installation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Installation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstallationRepository extends JpaRepository<Installation, Long> {}
