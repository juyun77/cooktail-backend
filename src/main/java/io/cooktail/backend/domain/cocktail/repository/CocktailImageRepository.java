package io.cooktail.backend.domain.cocktail.repository;

import io.cooktail.backend.domain.cocktail.domain.CocktailImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocktailImageRepository extends JpaRepository<CocktailImage, Long> {

}
