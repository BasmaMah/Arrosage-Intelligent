package com.ensaj.ma.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ensaj.ma.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FermeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ferme.class);
        Ferme ferme1 = new Ferme();
        ferme1.setId(1L);
        Ferme ferme2 = new Ferme();
        ferme2.setId(ferme1.getId());
        assertThat(ferme1).isEqualTo(ferme2);
        ferme2.setId(2L);
        assertThat(ferme1).isNotEqualTo(ferme2);
        ferme1.setId(null);
        assertThat(ferme1).isNotEqualTo(ferme2);
    }
}
