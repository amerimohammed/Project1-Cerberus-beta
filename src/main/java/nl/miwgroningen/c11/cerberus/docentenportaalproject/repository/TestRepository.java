package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Gemaakt door Marianne Kooistra (me.kooistra@st.hanze.nl) op 19/06/2023
 */
public interface TestRepository extends JpaRepository<Test, Long> {
}
