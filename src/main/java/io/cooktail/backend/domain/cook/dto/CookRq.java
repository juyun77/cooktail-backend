package io.cooktail.backend.domain.cook.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CookRq {

    private String title;
    private String recipe;
    private int difficulty;

    @Builder
    public CookRq(String title, String recipe, int difficulty) {
        this.title = title;
        this.recipe = recipe;
        this.difficulty = difficulty;
    }
}
