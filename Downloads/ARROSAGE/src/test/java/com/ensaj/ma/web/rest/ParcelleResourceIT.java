package com.ensaj.ma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensaj.ma.IntegrationTest;
import com.ensaj.ma.domain.Parcelle;
import com.ensaj.ma.repository.ParcelleRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ParcelleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParcelleResourceIT {

    private static final Float DEFAULT_SURFACE = 1F;
    private static final Float UPDATED_SURFACE = 2F;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parcelles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParcelleRepository parcelleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParcelleMockMvc;

    private Parcelle parcelle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcelle createEntity(EntityManager em) {
        Parcelle parcelle = new Parcelle()
            .surface(DEFAULT_SURFACE)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .libelle(DEFAULT_LIBELLE);
        return parcelle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcelle createUpdatedEntity(EntityManager em) {
        Parcelle parcelle = new Parcelle()
            .surface(UPDATED_SURFACE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .libelle(UPDATED_LIBELLE);
        return parcelle;
    }

    @BeforeEach
    public void initTest() {
        parcelle = createEntity(em);
    }

    @Test
    @Transactional
    void createParcelle() throws Exception {
        int databaseSizeBeforeCreate = parcelleRepository.findAll().size();
        // Create the Parcelle
        restParcelleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isCreated());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeCreate + 1);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getSurface()).isEqualTo(DEFAULT_SURFACE);
        assertThat(testParcelle.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testParcelle.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testParcelle.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void createParcelleWithExistingId() throws Exception {
        // Create the Parcelle with an existing ID
        parcelle.setId(1L);

        int databaseSizeBeforeCreate = parcelleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParcelleMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParcelles() throws Exception {
        // Initialize the database
        parcelleRepository.saveAndFlush(parcelle);

        // Get all the parcelleList
        restParcelleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parcelle.getId().intValue())))
            .andExpect(jsonPath("$.[*].surface").value(hasItem(DEFAULT_SURFACE.doubleValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getParcelle() throws Exception {
        // Initialize the database
        parcelleRepository.saveAndFlush(parcelle);

        // Get the parcelle
        restParcelleMockMvc
            .perform(get(ENTITY_API_URL_ID, parcelle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parcelle.getId().intValue()))
            .andExpect(jsonPath("$.surface").value(DEFAULT_SURFACE.doubleValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getNonExistingParcelle() throws Exception {
        // Get the parcelle
        restParcelleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewParcelle() throws Exception {
        // Initialize the database
        parcelleRepository.saveAndFlush(parcelle);

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();

        // Update the parcelle
        Parcelle updatedParcelle = parcelleRepository.findById(parcelle.getId()).get();
        // Disconnect from session so that the updates on updatedParcelle are not directly saved in db
        em.detach(updatedParcelle);
        updatedParcelle.surface(UPDATED_SURFACE).photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE).libelle(UPDATED_LIBELLE);

        restParcelleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedParcelle.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedParcelle))
            )
            .andExpect(status().isOk());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getSurface()).isEqualTo(UPDATED_SURFACE);
        assertThat(testParcelle.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testParcelle.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void putNonExistingParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();
        parcelle.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParcelleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parcelle.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();
        parcelle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();
        parcelle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelleMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParcelleWithPatch() throws Exception {
        // Initialize the database
        parcelleRepository.saveAndFlush(parcelle);

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();

        // Update the parcelle using partial update
        Parcelle partialUpdatedParcelle = new Parcelle();
        partialUpdatedParcelle.setId(parcelle.getId());

        partialUpdatedParcelle.photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE).libelle(UPDATED_LIBELLE);

        restParcelleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParcelle.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParcelle))
            )
            .andExpect(status().isOk());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getSurface()).isEqualTo(DEFAULT_SURFACE);
        assertThat(testParcelle.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testParcelle.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateParcelleWithPatch() throws Exception {
        // Initialize the database
        parcelleRepository.saveAndFlush(parcelle);

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();

        // Update the parcelle using partial update
        Parcelle partialUpdatedParcelle = new Parcelle();
        partialUpdatedParcelle.setId(parcelle.getId());

        partialUpdatedParcelle
            .surface(UPDATED_SURFACE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .libelle(UPDATED_LIBELLE);

        restParcelleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParcelle.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParcelle))
            )
            .andExpect(status().isOk());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getSurface()).isEqualTo(UPDATED_SURFACE);
        assertThat(testParcelle.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testParcelle.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();
        parcelle.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParcelleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parcelle.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();
        parcelle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().size();
        parcelle.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParcelleMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parcelle))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParcelle() throws Exception {
        // Initialize the database
        parcelleRepository.saveAndFlush(parcelle);

        int databaseSizeBeforeDelete = parcelleRepository.findAll().size();

        // Delete the parcelle
        restParcelleMockMvc
            .perform(delete(ENTITY_API_URL_ID, parcelle.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Parcelle> parcelleList = parcelleRepository.findAll();
        assertThat(parcelleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
