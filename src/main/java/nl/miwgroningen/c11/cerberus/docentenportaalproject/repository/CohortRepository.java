package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
    @Query("SELECT c FROM Cohort c JOIN c.programme p JOIN p.subjects s JOIN s.teachers t WHERE t.username = ?1")
    List<Cohort> findCohortsByUsername(String username);
}
