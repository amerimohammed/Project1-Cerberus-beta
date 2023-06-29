package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
    //Selects all cohorts a given teacher teaches
    @Query("SELECT DISTINCT cohort " +
            "FROM Cohort cohort JOIN cohort.programme programme " +
                "JOIN programme.subjects subjects " +
                    "JOIN subjects.teachers teachers " +
            "WHERE teachers.username = ?1")
    List<Cohort> findCohortsByTeacherUsername(String username);
}
