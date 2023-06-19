package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
