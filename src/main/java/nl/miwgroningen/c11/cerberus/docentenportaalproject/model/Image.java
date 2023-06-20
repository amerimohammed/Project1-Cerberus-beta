package nl.miwgroningen.c11.cerberus.docentenportaalproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * a photo link will be used for other entities
 *
 * @author Marianne Kooistra, Mohammed Almameri, Joost Schreuder 19/06/2023
 */
@Entity
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue
    private Long imageId;
    @Column(nullable = false)
    private String imageName;

    public String getImagePath (){
        return "/photos/" + imageName;
    }
}
