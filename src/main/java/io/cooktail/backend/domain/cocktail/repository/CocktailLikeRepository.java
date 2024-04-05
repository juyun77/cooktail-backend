package io.cooktail.backend.domain.cocktail.repository;

import io.cooktail.backend.domain.cocktail.domain.Cocktail;
import io.cooktail.backend.domain.cocktail.domain.CocktailLike;
import io.cooktail.backend.domain.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CocktailLikeRepository extends JpaRepository<CocktailLike, Long> {
  Boolean existsByMemberAndCocktail(Member member, Cocktail cocktail);

  Optional<CocktailLike> findByMemberAndCocktail(Member member, Cocktail cocktail);

  List<CocktailLike> findByMember(Member member);
}
