package nl.miwgroningen.c11.cerberus.docentenportaalproject.repository;

import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
