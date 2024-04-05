package io.cooktail.backend.domain.cocktail.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocktail_image")
@Getter
@NoArgsConstructor
public class CocktailImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cocktail_image_id")
  private long id;

  @Column(name = "image_url", nullable=false)
  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cocktail_id", nullable=false)
  private Cocktail cocktail;

  @Builder
  public CocktailImage(String imageUrl, Cocktail cocktail) {
    this.imageUrl = imageUrl;
    this.cocktail = cocktail;
  }
}