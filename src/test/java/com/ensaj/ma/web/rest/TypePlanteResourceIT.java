package com.ensaj.ma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensaj.ma.IntegrationTest;
import com.ensaj.ma.domain.TypePlante;
import com.ensaj.ma.repository.TypePlanteRepository;
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
 * Integration tests for the {@link TypePlanteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypePlanteResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final Float DEFAULT_HUMIDITE_MAX = 1F;
    private static final Float UPDATED_HUMIDITE_MAX = 2F;

    private static final Float DEFAULT_HUMIDITE_MIN = 1F;
    private static final Float UPDATED_HUMIDITE_MIN = 2F;

    private static final Float DEFAULT_TEMPERATURE = 1F;
    private static final Float UPDATED_TEMPERATURE = 2F;

    private static final Float DEFAULT_LUMINOSITE = 1F;
    private static final Float UPDATED_LUMINOSITE = 2F;

    private static final String ENTITY_API_URL = "/api/type-plantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypePlanteRepository typePlanteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypePlanteMockMvc;

    private TypePlante typePlante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypePlante createEntity(EntityManager em) {
        TypePlante typePlante = new TypePlante()
            .libelle(DEFAULT_LIBELLE)
            .humiditeMax(DEFAULT_HUMIDITE_MAX)
            .humiditeMin(DEFAULT_HUMIDITE_MIN)
            .temperature(DEFAULT_TEMPERATURE)
            .luminosite(DEFAULT_LUMINOSITE);
        return typePlante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypePlante createUpdatedEntity(EntityManager em) {
        TypePlante typePlante = new TypePlante()
            .libelle(UPDATED_LIBELLE)
            .humiditeMax(UPDATED_HUMIDITE_MAX)
            .humiditeMin(UPDATED_HUMIDITE_MIN)
            .temperature(UPDATED_TEMPERATURE)
            .luminosite(UPDATED_LUMINOSITE);
        return typePlante;
    }

    @BeforeEach
    public void initTest() {
        typePlante = createEntity(em);
    }

    @Test
    @Transactional
    void createTypePlante() throws Exception {
        int databaseSizeBeforeCreate = typePlanteRepository.findAll().size();
        // Create the TypePlante
        restTypePlanteMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isCreated());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeCreate + 1);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(DEFAULT_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(DEFAULT_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(DEFAULT_TEMPERATURE);
        assertThat(testTypePlante.getLuminosite()).isEqualTo(DEFAULT_LUMINOSITE);
    }

    @Test
    @Transactional
    void createTypePlanteWithExistingId() throws Exception {
        // Create the TypePlante with an existing ID
        typePlante.setId(1L);

        int databaseSizeBeforeCreate = typePlanteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypePlanteMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = typePlanteRepository.findAll().size();
        // set the field null
        typePlante.setLibelle(null);

        // Create the TypePlante, which fails.

        restTypePlanteMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isBadRequest());

        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTypePlantes() throws Exception {
        // Initialize the database
        typePlanteRepository.saveAndFlush(typePlante);

        // Get all the typePlanteList
        restTypePlanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typePlante.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].humiditeMax").value(hasItem(DEFAULT_HUMIDITE_MAX.doubleValue())))
            .andExpect(jsonPath("$.[*].humiditeMin").value(hasItem(DEFAULT_HUMIDITE_MIN.doubleValue())))
            .andExpect(jsonPath("$.[*].temperature").value(hasItem(DEFAULT_TEMPERATURE.doubleValue())))
            .andExpect(jsonPath("$.[*].luminosite").value(hasItem(DEFAULT_LUMINOSITE.doubleValue())));
    }

    @Test
    @Transactional
    void getTypePlante() throws Exception {
        // Initialize the database
        typePlanteRepository.saveAndFlush(typePlante);

        // Get the typePlante
        restTypePlanteMockMvc
            .perform(get(ENTITY_API_URL_ID, typePlante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typePlante.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.humiditeMax").value(DEFAULT_HUMIDITE_MAX.doubleValue()))
            .andExpect(jsonPath("$.humiditeMin").value(DEFAULT_HUMIDITE_MIN.doubleValue()))
            .andExpect(jsonPath("$.temperature").value(DEFAULT_TEMPERATURE.doubleValue()))
            .andExpect(jsonPath("$.luminosite").value(DEFAULT_LUMINOSITE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingTypePlante() throws Exception {
        // Get the typePlante
        restTypePlanteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypePlante() throws Exception {
        // Initialize the database
        typePlanteRepository.saveAndFlush(typePlante);

        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();

        // Update the typePlante
        TypePlante updatedTypePlante = typePlanteRepository.findById(typePlante.getId()).get();
        // Disconnect from session so that the updates on updatedTypePlante are not directly saved in db
        em.detach(updatedTypePlante);
        updatedTypePlante
            .libelle(UPDATED_LIBELLE)
            .humiditeMax(UPDATED_HUMIDITE_MAX)
            .humiditeMin(UPDATED_HUMIDITE_MIN)
            .temperature(UPDATED_TEMPERATURE)
            .luminosite(UPDATED_LUMINOSITE);

        restTypePlanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTypePlante.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTypePlante))
            )
            .andExpect(status().isOk());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(UPDATED_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(UPDATED_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(UPDATED_TEMPERATURE);
        assertThat(testTypePlante.getLuminosite()).isEqualTo(UPDATED_LUMINOSITE);
    }

    @Test
    @Transactional
    void putNonExistingTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();
        typePlante.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypePlanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typePlante.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();
        typePlante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();
        typePlante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlanteMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypePlanteWithPatch() throws Exception {
        // Initialize the database
        typePlanteRepository.saveAndFlush(typePlante);

        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();

        // Update the typePlante using partial update
        TypePlante partialUpdatedTypePlante = new TypePlante();
        partialUpdatedTypePlante.setId(typePlante.getId());

        partialUpdatedTypePlante.humiditeMax(UPDATED_HUMIDITE_MAX).temperature(UPDATED_TEMPERATURE).luminosite(UPDATED_LUMINOSITE);

        restTypePlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypePlante.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypePlante))
            )
            .andExpect(status().isOk());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(UPDATED_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(DEFAULT_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(UPDATED_TEMPERATURE);
        assertThat(testTypePlante.getLuminosite()).isEqualTo(UPDATED_LUMINOSITE);
    }

    @Test
    @Transactional
    void fullUpdateTypePlanteWithPatch() throws Exception {
        // Initialize the database
        typePlanteRepository.saveAndFlush(typePlante);

        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();

        // Update the typePlante using partial update
        TypePlante partialUpdatedTypePlante = new TypePlante();
        partialUpdatedTypePlante.setId(typePlante.getId());

        partialUpdatedTypePlante
            .libelle(UPDATED_LIBELLE)
            .humiditeMax(UPDATED_HUMIDITE_MAX)
            .humiditeMin(UPDATED_HUMIDITE_MIN)
            .temperature(UPDATED_TEMPERATURE)
            .luminosite(UPDATED_LUMINOSITE);

        restTypePlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypePlante.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypePlante))
            )
            .andExpect(status().isOk());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
        TypePlante testTypePlante = typePlanteList.get(typePlanteList.size() - 1);
        assertThat(testTypePlante.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testTypePlante.getHumiditeMax()).isEqualTo(UPDATED_HUMIDITE_MAX);
        assertThat(testTypePlante.getHumiditeMin()).isEqualTo(UPDATED_HUMIDITE_MIN);
        assertThat(testTypePlante.getTemperature()).isEqualTo(UPDATED_TEMPERATURE);
        assertThat(testTypePlante.getLuminosite()).isEqualTo(UPDATED_LUMINOSITE);
    }

    @Test
    @Transactional
    void patchNonExistingTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();
        typePlante.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypePlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typePlante.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();
        typePlante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypePlante() throws Exception {
        int databaseSizeBeforeUpdate = typePlanteRepository.findAll().size();
        typePlante.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlanteMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typePlante))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypePlante in the database
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypePlante() throws Exception {
        // Initialize the database
        typePlanteRepository.saveAndFlush(typePlante);

        int databaseSizeBeforeDelete = typePlanteRepository.findAll().size();

        // Delete the typePlante
        restTypePlanteMockMvc
            .perform(delete(ENTITY_API_URL_ID, typePlante.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypePlante> typePlanteList = typePlanteRepository.findAll();
        assertThat(typePlanteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
