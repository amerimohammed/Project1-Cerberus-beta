package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
