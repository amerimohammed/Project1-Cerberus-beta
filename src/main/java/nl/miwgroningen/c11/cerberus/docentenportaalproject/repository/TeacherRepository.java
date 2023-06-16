package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
