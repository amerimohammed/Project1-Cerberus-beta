package nl.miwgroningen.c11.cerberus.docentenportaalproject.dto;

import lombok.Builder;
import lombok.Data;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test;

@Data @Builder
public class subTestDTO {
    private Test superTest;
    private Test subTest;
}
