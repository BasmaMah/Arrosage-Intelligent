package com.ensaj.ma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensaj.ma.IntegrationTest;
import com.ensaj.ma.domain.Plantage;
import com.ensaj.ma.repository.PlantageRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PlantageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlantageResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_NOMBRE = 1;
    private static final Integer UPDATED_NOMBRE = 2;

    private static final String ENTITY_API_URL = "/api/plantages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlantageRepository plantageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlantageMockMvc;

    private Plantage plantage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plantage createEntity(EntityManager em) {
        Plantage plantage = new Plantage().date(DEFAULT_DATE).nombre(DEFAULT_NOMBRE);
        return plantage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plantage createUpdatedEntity(EntityManager em) {
        Plantage plantage = new Plantage().date(UPDATED_DATE).nombre(UPDATED_NOMBRE);
        return plantage;
    }

    @BeforeEach
    public void initTest() {
        plantage = createEntity(em);
    }

    @Test
    @Transactional
    void createPlantage() throws Exception {
        int databaseSizeBeforeCreate = plantageRepository.findAll().size();
        // Create the Plantage
        restPlantageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isCreated());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeCreate + 1);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    void createPlantageWithExistingId() throws Exception {
        // Create the Plantage with an existing ID
        plantage.setId(1L);

        int databaseSizeBeforeCreate = plantageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlantageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = plantageRepository.findAll().size();
        // set the field null
        plantage.setDate(null);

        // Create the Plantage, which fails.

        restPlantageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = plantageRepository.findAll().size();
        // set the field null
        plantage.setNombre(null);

        // Create the Plantage, which fails.

        restPlantageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlantages() throws Exception {
        // Initialize the database
        plantageRepository.saveAndFlush(plantage);

        // Get all the plantageList
        restPlantageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plantage.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)));
    }

    @Test
    @Transactional
    void getPlantage() throws Exception {
        // Initialize the database
        plantageRepository.saveAndFlush(plantage);

        // Get the plantage
        restPlantageMockMvc
            .perform(get(ENTITY_API_URL_ID, plantage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plantage.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE));
    }

    @Test
    @Transactional
    void getNonExistingPlantage() throws Exception {
        // Get the plantage
        restPlantageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlantage() throws Exception {
        // Initialize the database
        plantageRepository.saveAndFlush(plantage);

        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();

        // Update the plantage
        Plantage updatedPlantage = plantageRepository.findById(plantage.getId()).get();
        // Disconnect from session so that the updates on updatedPlantage are not directly saved in db
        em.detach(updatedPlantage);
        updatedPlantage.date(UPDATED_DATE).nombre(UPDATED_NOMBRE);

        restPlantageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlantage.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlantage))
            )
            .andExpect(status().isOk());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void putNonExistingPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();
        plantage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlantageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, plantage.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();
        plantage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlantageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();
        plantage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlantageMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlantageWithPatch() throws Exception {
        // Initialize the database
        plantageRepository.saveAndFlush(plantage);

        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();

        // Update the plantage using partial update
        Plantage partialUpdatedPlantage = new Plantage();
        partialUpdatedPlantage.setId(plantage.getId());

        partialUpdatedPlantage.date(UPDATED_DATE);

        restPlantageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlantage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlantage))
            )
            .andExpect(status().isOk());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    void fullUpdatePlantageWithPatch() throws Exception {
        // Initialize the database
        plantageRepository.saveAndFlush(plantage);

        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();

        // Update the plantage using partial update
        Plantage partialUpdatedPlantage = new Plantage();
        partialUpdatedPlantage.setId(plantage.getId());

        partialUpdatedPlantage.date(UPDATED_DATE).nombre(UPDATED_NOMBRE);

        restPlantageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlantage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlantage))
            )
            .andExpect(status().isOk());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
        Plantage testPlantage = plantageList.get(plantageList.size() - 1);
        assertThat(testPlantage.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPlantage.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void patchNonExistingPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();
        plantage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlantageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, plantage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();
        plantage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlantageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlantage() throws Exception {
        int databaseSizeBeforeUpdate = plantageRepository.findAll().size();
        plantage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlantageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(plantage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plantage in the database
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlantage() throws Exception {
        // Initialize the database
        plantageRepository.saveAndFlush(plantage);

        int databaseSizeBeforeDelete = plantageRepository.findAll().size();

        // Delete the plantage
        restPlantageMockMvc
            .perform(delete(ENTITY_API_URL_ID, plantage.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Plantage> plantageList = plantageRepository.findAll();
        assertThat(plantageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
