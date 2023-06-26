package nl.miwgroningen.c11.cerberus.docentenportaalproject.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import nl.miwgroningen.c11.cerberus.docentenportaalproject.model.Test;

@Data @Builder
@Getter
public class subTestDTO {
    private Test superTest;
    private Test subTest;
}
