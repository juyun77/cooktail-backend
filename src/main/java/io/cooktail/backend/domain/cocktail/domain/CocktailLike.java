package io.cooktail.backend.domain.cocktail.domain;

import io.cooktail.backend.domain.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cocktail_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CocktailLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "like_id", updatable = false)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cocktail_id")
  private Cocktail cocktail;

  @Builder
  public CocktailLike(Member member, Cocktail cocktail) {
    this.member = member;
    this.cocktail = cocktail;
  }
}
