package com.ensaj.ma.web.rest;

import com.ensaj.ma.domain.Plantage;
import com.ensaj.ma.repository.PlantageRepository;
import com.ensaj.ma.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ensaj.ma.domain.Plantage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PlantageResource {

    private final Logger log = LoggerFactory.getLogger(PlantageResource.class);

    private static final String ENTITY_NAME = "plantage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlantageRepository plantageRepository;

    public PlantageResource(PlantageRepository plantageRepository) {
        this.plantageRepository = plantageRepository;
    }

    /**
     * {@code POST  /plantages} : Create a new plantage.
     *
     * @param plantage the plantage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new plantage, or with status {@code 400 (Bad Request)} if the plantage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/plantages")
    public ResponseEntity<Plantage> createPlantage(@Valid @RequestBody Plantage plantage) throws URISyntaxException {
        log.debug("REST request to save Plantage : {}", plantage);
        if (plantage.getId() != null) {
            throw new BadRequestAlertException("A new plantage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Plantage result = plantageRepository.save(plantage);
        return ResponseEntity
            .created(new URI("/api/plantages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /plantages/:id} : Updates an existing plantage.
     *
     * @param id the id of the plantage to save.
     * @param plantage the plantage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plantage,
     * or with status {@code 400 (Bad Request)} if the plantage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the plantage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/plantages/{id}")
    public ResponseEntity<Plantage> updatePlantage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Plantage plantage
    ) throws URISyntaxException {
        log.debug("REST request to update Plantage : {}, {}", id, plantage);
        if (plantage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plantage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!plantageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Plantage result = plantageRepository.save(plantage);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, plantage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /plantages/:id} : Partial updates given fields of an existing plantage, field will ignore if it is null
     *
     * @param id the id of the plantage to save.
     * @param plantage the plantage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated plantage,
     * or with status {@code 400 (Bad Request)} if the plantage is not valid,
     * or with status {@code 404 (Not Found)} if the plantage is not found,
     * or with status {@code 500 (Internal Server Error)} if the plantage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/plantages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Plantage> partialUpdatePlantage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Plantage plantage
    ) throws URISyntaxException {
        log.debug("REST request to partial update Plantage partially : {}, {}", id, plantage);
        if (plantage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, plantage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!plantageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Plantage> result = plantageRepository
            .findById(plantage.getId())
            .map(existingPlantage -> {
                if (plantage.getDate() != null) {
                    existingPlantage.setDate(plantage.getDate());
                }
                if (plantage.getNombre() != null) {
                    existingPlantage.setNombre(plantage.getNombre());
                }

                return existingPlantage;
            })
            .map(plantageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, plantage.getId().toString())
        );
    }

    /**
     * {@code GET  /plantages} : get all the plantages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of plantages in body.
     */
    @GetMapping("/plantages")
    public List<Plantage> getAllPlantages() {
        log.debug("REST request to get all Plantages");
        return plantageRepository.findAll();
    }

    /**
     * {@code GET  /plantages/:id} : get the "id" plantage.
     *
     * @param id the id of the plantage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the plantage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/plantages/{id}")
    public ResponseEntity<Plantage> getPlantage(@PathVariable Long id) {
        log.debug("REST request to get Plantage : {}", id);
        Optional<Plantage> plantage = plantageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(plantage);
    }

    /**
     * {@code DELETE  /plantages/:id} : delete the "id" plantage.
     *
     * @param id the id of the plantage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/plantages/{id}")
    public ResponseEntity<Void> deletePlantage(@PathVariable Long id) {
        log.debug("REST request to delete Plantage : {}", id);
        plantageRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
