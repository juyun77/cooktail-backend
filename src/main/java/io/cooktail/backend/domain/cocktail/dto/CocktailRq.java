package io.cooktail.backend.domain.cocktail.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CocktailRq {

  private String title;
  private String description;
  private String ingredient;
  private String recipe;
  private double abv;

  @Builder
  public CocktailRq(String title, String description, String ingredient, String recipe, double abv) {
    this.title = title;
    this.description = description;
    this.ingredient = ingredient;
    this.recipe = recipe;
    this.abv = abv;
  }
}
