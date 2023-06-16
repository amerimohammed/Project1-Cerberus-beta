package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User access rights in the app
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder 15/06/2023
 */

@Entity
@Getter @Setter
public class Role {

    @Id
    @GeneratedValue
    private Long roleId;

    @Column(unique = true, nullable = false)
    private String roleName;
}
