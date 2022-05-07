package com.ensaj.ma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensaj.ma.IntegrationTest;
import com.ensaj.ma.domain.Boitier;
import com.ensaj.ma.repository.BoitierRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BoitierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BoitierResourceIT {

    private static final Integer DEFAULT_REF = 1;
    private static final Integer UPDATED_REF = 2;

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NBR_BRANCH_BOITIER = 1;
    private static final Integer UPDATED_NBR_BRANCH_BOITIER = 2;

    private static final Integer DEFAULT_NBR_BRANCH_ARDUINO = 1;
    private static final Integer UPDATED_NBR_BRANCH_ARDUINO = 2;

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/boitiers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BoitierRepository boitierRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoitierMockMvc;

    private Boitier boitier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boitier createEntity(EntityManager em) {
        Boitier boitier = new Boitier()
            .ref(DEFAULT_REF)
            .type(DEFAULT_TYPE)
            .nbrBranchBoitier(DEFAULT_NBR_BRANCH_BOITIER)
            .nbrBranchArduino(DEFAULT_NBR_BRANCH_ARDUINO)
            .code(DEFAULT_CODE);
        return boitier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boitier createUpdatedEntity(EntityManager em) {
        Boitier boitier = new Boitier()
            .ref(UPDATED_REF)
            .type(UPDATED_TYPE)
            .nbrBranchBoitier(UPDATED_NBR_BRANCH_BOITIER)
            .nbrBranchArduino(UPDATED_NBR_BRANCH_ARDUINO)
            .code(UPDATED_CODE);
        return boitier;
    }

    @BeforeEach
    public void initTest() {
        boitier = createEntity(em);
    }

    @Test
    @Transactional
    void createBoitier() throws Exception {
        int databaseSizeBeforeCreate = boitierRepository.findAll().size();
        // Create the Boitier
        restBoitierMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isCreated());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeCreate + 1);
        Boitier testBoitier = boitierList.get(boitierList.size() - 1);
        assertThat(testBoitier.getRef()).isEqualTo(DEFAULT_REF);
        assertThat(testBoitier.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBoitier.getNbrBranchBoitier()).isEqualTo(DEFAULT_NBR_BRANCH_BOITIER);
        assertThat(testBoitier.getNbrBranchArduino()).isEqualTo(DEFAULT_NBR_BRANCH_ARDUINO);
        assertThat(testBoitier.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void createBoitierWithExistingId() throws Exception {
        // Create the Boitier with an existing ID
        boitier.setId(1L);

        int databaseSizeBeforeCreate = boitierRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoitierMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRefIsRequired() throws Exception {
        int databaseSizeBeforeTest = boitierRepository.findAll().size();
        // set the field null
        boitier.setRef(null);

        // Create the Boitier, which fails.

        restBoitierMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isBadRequest());

        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoitiers() throws Exception {
        // Initialize the database
        boitierRepository.saveAndFlush(boitier);

        // Get all the boitierList
        restBoitierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boitier.getId().intValue())))
            .andExpect(jsonPath("$.[*].ref").value(hasItem(DEFAULT_REF)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].nbrBranchBoitier").value(hasItem(DEFAULT_NBR_BRANCH_BOITIER)))
            .andExpect(jsonPath("$.[*].nbrBranchArduino").value(hasItem(DEFAULT_NBR_BRANCH_ARDUINO)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getBoitier() throws Exception {
        // Initialize the database
        boitierRepository.saveAndFlush(boitier);

        // Get the boitier
        restBoitierMockMvc
            .perform(get(ENTITY_API_URL_ID, boitier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boitier.getId().intValue()))
            .andExpect(jsonPath("$.ref").value(DEFAULT_REF))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.nbrBranchBoitier").value(DEFAULT_NBR_BRANCH_BOITIER))
            .andExpect(jsonPath("$.nbrBranchArduino").value(DEFAULT_NBR_BRANCH_ARDUINO))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingBoitier() throws Exception {
        // Get the boitier
        restBoitierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBoitier() throws Exception {
        // Initialize the database
        boitierRepository.saveAndFlush(boitier);

        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();

        // Update the boitier
        Boitier updatedBoitier = boitierRepository.findById(boitier.getId()).get();
        // Disconnect from session so that the updates on updatedBoitier are not directly saved in db
        em.detach(updatedBoitier);
        updatedBoitier
            .ref(UPDATED_REF)
            .type(UPDATED_TYPE)
            .nbrBranchBoitier(UPDATED_NBR_BRANCH_BOITIER)
            .nbrBranchArduino(UPDATED_NBR_BRANCH_ARDUINO)
            .code(UPDATED_CODE);

        restBoitierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBoitier.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBoitier))
            )
            .andExpect(status().isOk());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
        Boitier testBoitier = boitierList.get(boitierList.size() - 1);
        assertThat(testBoitier.getRef()).isEqualTo(UPDATED_REF);
        assertThat(testBoitier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBoitier.getNbrBranchBoitier()).isEqualTo(UPDATED_NBR_BRANCH_BOITIER);
        assertThat(testBoitier.getNbrBranchArduino()).isEqualTo(UPDATED_NBR_BRANCH_ARDUINO);
        assertThat(testBoitier.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void putNonExistingBoitier() throws Exception {
        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();
        boitier.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoitierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boitier.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoitier() throws Exception {
        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();
        boitier.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoitierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoitier() throws Exception {
        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();
        boitier.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoitierMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoitierWithPatch() throws Exception {
        // Initialize the database
        boitierRepository.saveAndFlush(boitier);

        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();

        // Update the boitier using partial update
        Boitier partialUpdatedBoitier = new Boitier();
        partialUpdatedBoitier.setId(boitier.getId());

        partialUpdatedBoitier
            .type(UPDATED_TYPE)
            .nbrBranchBoitier(UPDATED_NBR_BRANCH_BOITIER)
            .nbrBranchArduino(UPDATED_NBR_BRANCH_ARDUINO)
            .code(UPDATED_CODE);

        restBoitierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoitier.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBoitier))
            )
            .andExpect(status().isOk());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
        Boitier testBoitier = boitierList.get(boitierList.size() - 1);
        assertThat(testBoitier.getRef()).isEqualTo(DEFAULT_REF);
        assertThat(testBoitier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBoitier.getNbrBranchBoitier()).isEqualTo(UPDATED_NBR_BRANCH_BOITIER);
        assertThat(testBoitier.getNbrBranchArduino()).isEqualTo(UPDATED_NBR_BRANCH_ARDUINO);
        assertThat(testBoitier.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void fullUpdateBoitierWithPatch() throws Exception {
        // Initialize the database
        boitierRepository.saveAndFlush(boitier);

        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();

        // Update the boitier using partial update
        Boitier partialUpdatedBoitier = new Boitier();
        partialUpdatedBoitier.setId(boitier.getId());

        partialUpdatedBoitier
            .ref(UPDATED_REF)
            .type(UPDATED_TYPE)
            .nbrBranchBoitier(UPDATED_NBR_BRANCH_BOITIER)
            .nbrBranchArduino(UPDATED_NBR_BRANCH_ARDUINO)
            .code(UPDATED_CODE);

        restBoitierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoitier.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBoitier))
            )
            .andExpect(status().isOk());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
        Boitier testBoitier = boitierList.get(boitierList.size() - 1);
        assertThat(testBoitier.getRef()).isEqualTo(UPDATED_REF);
        assertThat(testBoitier.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBoitier.getNbrBranchBoitier()).isEqualTo(UPDATED_NBR_BRANCH_BOITIER);
        assertThat(testBoitier.getNbrBranchArduino()).isEqualTo(UPDATED_NBR_BRANCH_ARDUINO);
        assertThat(testBoitier.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingBoitier() throws Exception {
        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();
        boitier.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoitierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boitier.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoitier() throws Exception {
        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();
        boitier.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoitierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoitier() throws Exception {
        int databaseSizeBeforeUpdate = boitierRepository.findAll().size();
        boitier.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoitierMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boitier))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boitier in the database
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoitier() throws Exception {
        // Initialize the database
        boitierRepository.saveAndFlush(boitier);

        int databaseSizeBeforeDelete = boitierRepository.findAll().size();

        // Delete the boitier
        restBoitierMockMvc
            .perform(delete(ENTITY_API_URL_ID, boitier.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Boitier> boitierList = boitierRepository.findAll();
        assertThat(boitierList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
