package com.ensaj.ma.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ensaj.ma.IntegrationTest;
import com.ensaj.ma.domain.Capteur;
import com.ensaj.ma.repository.CapteurRepository;
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
 * Integration tests for the {@link CapteurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CapteurResourceIT {

    private static final String DEFAULT_REF = "AAAAAAAAAA";
    private static final String UPDATED_REF = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/capteurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CapteurRepository capteurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCapteurMockMvc;

    private Capteur capteur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Capteur createEntity(EntityManager em) {
        Capteur capteur = new Capteur()
            .ref(DEFAULT_REF)
            .type(DEFAULT_TYPE)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE);
        return capteur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Capteur createUpdatedEntity(EntityManager em) {
        Capteur capteur = new Capteur()
            .ref(UPDATED_REF)
            .type(UPDATED_TYPE)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);
        return capteur;
    }

    @BeforeEach
    public void initTest() {
        capteur = createEntity(em);
    }

    @Test
    @Transactional
    void createCapteur() throws Exception {
        int databaseSizeBeforeCreate = capteurRepository.findAll().size();
        // Create the Capteur
        restCapteurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isCreated());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeCreate + 1);
        Capteur testCapteur = capteurList.get(capteurList.size() - 1);
        assertThat(testCapteur.getRef()).isEqualTo(DEFAULT_REF);
        assertThat(testCapteur.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCapteur.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testCapteur.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createCapteurWithExistingId() throws Exception {
        // Create the Capteur with an existing ID
        capteur.setId(1L);

        int databaseSizeBeforeCreate = capteurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapteurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCapteurs() throws Exception {
        // Initialize the database
        capteurRepository.saveAndFlush(capteur);

        // Get all the capteurList
        restCapteurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].ref").value(hasItem(DEFAULT_REF)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))));
    }

    @Test
    @Transactional
    void getCapteur() throws Exception {
        // Initialize the database
        capteurRepository.saveAndFlush(capteur);

        // Get the capteur
        restCapteurMockMvc
            .perform(get(ENTITY_API_URL_ID, capteur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(capteur.getId().intValue()))
            .andExpect(jsonPath("$.ref").value(DEFAULT_REF))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)));
    }

    @Test
    @Transactional
    void getNonExistingCapteur() throws Exception {
        // Get the capteur
        restCapteurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCapteur() throws Exception {
        // Initialize the database
        capteurRepository.saveAndFlush(capteur);

        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();

        // Update the capteur
        Capteur updatedCapteur = capteurRepository.findById(capteur.getId()).get();
        // Disconnect from session so that the updates on updatedCapteur are not directly saved in db
        em.detach(updatedCapteur);
        updatedCapteur.ref(UPDATED_REF).type(UPDATED_TYPE).photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restCapteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCapteur.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCapteur))
            )
            .andExpect(status().isOk());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
        Capteur testCapteur = capteurList.get(capteurList.size() - 1);
        assertThat(testCapteur.getRef()).isEqualTo(UPDATED_REF);
        assertThat(testCapteur.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCapteur.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testCapteur.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingCapteur() throws Exception {
        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();
        capteur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, capteur.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCapteur() throws Exception {
        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();
        capteur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapteurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCapteur() throws Exception {
        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();
        capteur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapteurMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCapteurWithPatch() throws Exception {
        // Initialize the database
        capteurRepository.saveAndFlush(capteur);

        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();

        // Update the capteur using partial update
        Capteur partialUpdatedCapteur = new Capteur();
        partialUpdatedCapteur.setId(capteur.getId());

        partialUpdatedCapteur.ref(UPDATED_REF);

        restCapteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapteur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCapteur))
            )
            .andExpect(status().isOk());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
        Capteur testCapteur = capteurList.get(capteurList.size() - 1);
        assertThat(testCapteur.getRef()).isEqualTo(UPDATED_REF);
        assertThat(testCapteur.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCapteur.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testCapteur.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateCapteurWithPatch() throws Exception {
        // Initialize the database
        capteurRepository.saveAndFlush(capteur);

        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();

        // Update the capteur using partial update
        Capteur partialUpdatedCapteur = new Capteur();
        partialUpdatedCapteur.setId(capteur.getId());

        partialUpdatedCapteur.ref(UPDATED_REF).type(UPDATED_TYPE).photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restCapteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapteur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCapteur))
            )
            .andExpect(status().isOk());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
        Capteur testCapteur = capteurList.get(capteurList.size() - 1);
        assertThat(testCapteur.getRef()).isEqualTo(UPDATED_REF);
        assertThat(testCapteur.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCapteur.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testCapteur.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingCapteur() throws Exception {
        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();
        capteur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, capteur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCapteur() throws Exception {
        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();
        capteur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapteurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCapteur() throws Exception {
        int databaseSizeBeforeUpdate = capteurRepository.findAll().size();
        capteur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapteurMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(capteur))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Capteur in the database
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCapteur() throws Exception {
        // Initialize the database
        capteurRepository.saveAndFlush(capteur);

        int databaseSizeBeforeDelete = capteurRepository.findAll().size();

        // Delete the capteur
        restCapteurMockMvc
            .perform(delete(ENTITY_API_URL_ID, capteur.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Capteur> capteurList = capteurRepository.findAll();
        assertThat(capteurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
