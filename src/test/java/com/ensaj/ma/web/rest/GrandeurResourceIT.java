package com.ensaj.ma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensaj.ma.IntegrationTest;
import com.ensaj.ma.domain.Grandeur;
import com.ensaj.ma.repository.GrandeurRepository;
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
 * Integration tests for the {@link GrandeurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GrandeurResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Float DEFAULT_VALEUR = 1F;
    private static final Float UPDATED_VALEUR = 2F;

    private static final String DEFAULT_UNITE = "AAAAAAAAAA";
    private static final String UPDATED_UNITE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/grandeurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GrandeurRepository grandeurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGrandeurMockMvc;

    private Grandeur grandeur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grandeur createEntity(EntityManager em) {
        Grandeur grandeur = new Grandeur().type(DEFAULT_TYPE).valeur(DEFAULT_VALEUR).unite(DEFAULT_UNITE).date(DEFAULT_DATE);
        return grandeur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Grandeur createUpdatedEntity(EntityManager em) {
        Grandeur grandeur = new Grandeur().type(UPDATED_TYPE).valeur(UPDATED_VALEUR).unite(UPDATED_UNITE).date(UPDATED_DATE);
        return grandeur;
    }

    @BeforeEach
    public void initTest() {
        grandeur = createEntity(em);
    }

    @Test
    @Transactional
    void createGrandeur() throws Exception {
        int databaseSizeBeforeCreate = grandeurRepository.findAll().size();
        // Create the Grandeur
        restGrandeurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isCreated());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeCreate + 1);
        Grandeur testGrandeur = grandeurList.get(grandeurList.size() - 1);
        assertThat(testGrandeur.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testGrandeur.getValeur()).isEqualTo(DEFAULT_VALEUR);
        assertThat(testGrandeur.getUnite()).isEqualTo(DEFAULT_UNITE);
        assertThat(testGrandeur.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createGrandeurWithExistingId() throws Exception {
        // Create the Grandeur with an existing ID
        grandeur.setId(1L);

        int databaseSizeBeforeCreate = grandeurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGrandeurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = grandeurRepository.findAll().size();
        // set the field null
        grandeur.setType(null);

        // Create the Grandeur, which fails.

        restGrandeurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValeurIsRequired() throws Exception {
        int databaseSizeBeforeTest = grandeurRepository.findAll().size();
        // set the field null
        grandeur.setValeur(null);

        // Create the Grandeur, which fails.

        restGrandeurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUniteIsRequired() throws Exception {
        int databaseSizeBeforeTest = grandeurRepository.findAll().size();
        // set the field null
        grandeur.setUnite(null);

        // Create the Grandeur, which fails.

        restGrandeurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = grandeurRepository.findAll().size();
        // set the field null
        grandeur.setDate(null);

        // Create the Grandeur, which fails.

        restGrandeurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGrandeurs() throws Exception {
        // Initialize the database
        grandeurRepository.saveAndFlush(grandeur);

        // Get all the grandeurList
        restGrandeurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(grandeur.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR.doubleValue())))
            .andExpect(jsonPath("$.[*].unite").value(hasItem(DEFAULT_UNITE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getGrandeur() throws Exception {
        // Initialize the database
        grandeurRepository.saveAndFlush(grandeur);

        // Get the grandeur
        restGrandeurMockMvc
            .perform(get(ENTITY_API_URL_ID, grandeur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(grandeur.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.valeur").value(DEFAULT_VALEUR.doubleValue()))
            .andExpect(jsonPath("$.unite").value(DEFAULT_UNITE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGrandeur() throws Exception {
        // Get the grandeur
        restGrandeurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGrandeur() throws Exception {
        // Initialize the database
        grandeurRepository.saveAndFlush(grandeur);

        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();

        // Update the grandeur
        Grandeur updatedGrandeur = grandeurRepository.findById(grandeur.getId()).get();
        // Disconnect from session so that the updates on updatedGrandeur are not directly saved in db
        em.detach(updatedGrandeur);
        updatedGrandeur.type(UPDATED_TYPE).valeur(UPDATED_VALEUR).unite(UPDATED_UNITE).date(UPDATED_DATE);

        restGrandeurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGrandeur.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGrandeur))
            )
            .andExpect(status().isOk());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
        Grandeur testGrandeur = grandeurList.get(grandeurList.size() - 1);
        assertThat(testGrandeur.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testGrandeur.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testGrandeur.getUnite()).isEqualTo(UPDATED_UNITE);
        assertThat(testGrandeur.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingGrandeur() throws Exception {
        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();
        grandeur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGrandeurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, grandeur.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGrandeur() throws Exception {
        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();
        grandeur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrandeurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGrandeur() throws Exception {
        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();
        grandeur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrandeurMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGrandeurWithPatch() throws Exception {
        // Initialize the database
        grandeurRepository.saveAndFlush(grandeur);

        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();

        // Update the grandeur using partial update
        Grandeur partialUpdatedGrandeur = new Grandeur();
        partialUpdatedGrandeur.setId(grandeur.getId());

        partialUpdatedGrandeur.type(UPDATED_TYPE).valeur(UPDATED_VALEUR).unite(UPDATED_UNITE).date(UPDATED_DATE);

        restGrandeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGrandeur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGrandeur))
            )
            .andExpect(status().isOk());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
        Grandeur testGrandeur = grandeurList.get(grandeurList.size() - 1);
        assertThat(testGrandeur.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testGrandeur.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testGrandeur.getUnite()).isEqualTo(UPDATED_UNITE);
        assertThat(testGrandeur.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateGrandeurWithPatch() throws Exception {
        // Initialize the database
        grandeurRepository.saveAndFlush(grandeur);

        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();

        // Update the grandeur using partial update
        Grandeur partialUpdatedGrandeur = new Grandeur();
        partialUpdatedGrandeur.setId(grandeur.getId());

        partialUpdatedGrandeur.type(UPDATED_TYPE).valeur(UPDATED_VALEUR).unite(UPDATED_UNITE).date(UPDATED_DATE);

        restGrandeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGrandeur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGrandeur))
            )
            .andExpect(status().isOk());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
        Grandeur testGrandeur = grandeurList.get(grandeurList.size() - 1);
        assertThat(testGrandeur.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testGrandeur.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testGrandeur.getUnite()).isEqualTo(UPDATED_UNITE);
        assertThat(testGrandeur.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingGrandeur() throws Exception {
        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();
        grandeur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGrandeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, grandeur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGrandeur() throws Exception {
        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();
        grandeur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrandeurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGrandeur() throws Exception {
        int databaseSizeBeforeUpdate = grandeurRepository.findAll().size();
        grandeur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGrandeurMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(grandeur))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Grandeur in the database
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGrandeur() throws Exception {
        // Initialize the database
        grandeurRepository.saveAndFlush(grandeur);

        int databaseSizeBeforeDelete = grandeurRepository.findAll().size();

        // Delete the grandeur
        restGrandeurMockMvc
            .perform(delete(ENTITY_API_URL_ID, grandeur.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Grandeur> grandeurList = grandeurRepository.findAll();
        assertThat(grandeurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
