package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Subject;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByTeachersContains(Teacher teacher);

    @Query("SELECT s FROM Subject s JOIN s.programmes p JOIN p.cohorts c JOIN c.students st WHERE st.username = ?1")
    List<Subject> findSubjectsByStudentUsername(String username);
}
