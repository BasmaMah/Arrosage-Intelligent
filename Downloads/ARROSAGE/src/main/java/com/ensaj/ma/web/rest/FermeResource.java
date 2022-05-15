package com.ensaj.ma.web.rest;

import com.ensaj.ma.domain.Ferme;
import com.ensaj.ma.repository.FermeRepository;
import com.ensaj.ma.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ensaj.ma.domain.Ferme}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FermeResource {

    private final Logger log = LoggerFactory.getLogger(FermeResource.class);

    private static final String ENTITY_NAME = "ferme";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FermeRepository fermeRepository;

    public FermeResource(FermeRepository fermeRepository) {
        this.fermeRepository = fermeRepository;
    }

    /**
     * {@code POST  /fermes} : Create a new ferme.
     *
     * @param ferme the ferme to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ferme, or with status {@code 400 (Bad Request)} if the ferme has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fermes")
    public ResponseEntity<Ferme> createFerme(@RequestBody Ferme ferme) throws URISyntaxException {
        log.debug("REST request to save Ferme : {}", ferme);
        if (ferme.getId() != null) {
            throw new BadRequestAlertException("A new ferme cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ferme result = fermeRepository.save(ferme);
        return ResponseEntity
            .created(new URI("/api/fermes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /fermes/:id} : Updates an existing ferme.
     *
     * @param id the id of the ferme to save.
     * @param ferme the ferme to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ferme,
     * or with status {@code 400 (Bad Request)} if the ferme is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ferme couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fermes/{id}")
    public ResponseEntity<Ferme> updateFerme(@PathVariable(value = "id", required = false) final Long id, @RequestBody Ferme ferme)
        throws URISyntaxException {
        log.debug("REST request to update Ferme : {}, {}", id, ferme);
        if (ferme.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ferme.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fermeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Ferme result = fermeRepository.save(ferme);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ferme.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /fermes/:id} : Partial updates given fields of an existing ferme, field will ignore if it is null
     *
     * @param id the id of the ferme to save.
     * @param ferme the ferme to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ferme,
     * or with status {@code 400 (Bad Request)} if the ferme is not valid,
     * or with status {@code 404 (Not Found)} if the ferme is not found,
     * or with status {@code 500 (Internal Server Error)} if the ferme couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fermes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Ferme> partialUpdateFerme(@PathVariable(value = "id", required = false) final Long id, @RequestBody Ferme ferme)
        throws URISyntaxException {
        log.debug("REST request to partial update Ferme partially : {}, {}", id, ferme);
        if (ferme.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ferme.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fermeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Ferme> result = fermeRepository
            .findById(ferme.getId())
            .map(existingFerme -> {
                if (ferme.getLebelle() != null) {
                    existingFerme.setLebelle(ferme.getLebelle());
                }
                if (ferme.getPhoto() != null) {
                    existingFerme.setPhoto(ferme.getPhoto());
                }
                if (ferme.getPhotoContentType() != null) {
                    existingFerme.setPhotoContentType(ferme.getPhotoContentType());
                }

                return existingFerme;
            })
            .map(fermeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ferme.getId().toString())
        );
    }

    /**
     * {@code GET  /fermes} : get all the fermes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fermes in body.
     */
    @GetMapping("/fermes")
    public List<Ferme> getAllFermes() {
        log.debug("REST request to get all Fermes");
        return fermeRepository.findAll();
    }

    /**
     * {@code GET  /fermes/:id} : get the "id" ferme.
     *
     * @param id the id of the ferme to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ferme, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fermes/{id}")
    public ResponseEntity<Ferme> getFerme(@PathVariable Long id) {
        log.debug("REST request to get Ferme : {}", id);
        Optional<Ferme> ferme = fermeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ferme);
    }

    /**
     * {@code DELETE  /fermes/:id} : delete the "id" ferme.
     *
     * @param id the id of the ferme to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fermes/{id}")
    public ResponseEntity<Void> deleteFerme(@PathVariable Long id) {
        log.debug("REST request to delete Ferme : {}", id);
        fermeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
