package io.cooktail.backend.domain.cocktail.dto;

import io.cooktail.backend.domain.cocktail.domain.Cocktail;
import io.cooktail.backend.domain.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CocktailRs {
  private long id;
  private String title;
  private String description;
  private String ingredient;
  private String recipe;
  private double abv;
  private Member member;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private int views;
  private int likes;
  private List<String> images;

  @Builder
  public CocktailRs(Cocktail cocktail, List<String> images) {
    this.id = cocktail.getId();
    this.title = cocktail.getTitle();
    this.description = cocktail.getDescription();
    this.ingredient = cocktail.getIngredient();
    this.recipe = cocktail.getRecipe();
    this.abv = cocktail.getAbv();
    this.member = cocktail.getMember();
    this.createdAt = cocktail.getCreatedAt();
    this.updatedAt = cocktail.getUpdatedAt();
    this.views = cocktail.getViews();
    this.likes = cocktail.getLikesCount();
    this.images = images;
  }
}
