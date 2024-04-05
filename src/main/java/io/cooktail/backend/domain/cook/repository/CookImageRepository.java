package io.cooktail.backend.domain.cook.repository;

import io.cooktail.backend.domain.cook.domain.CookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookImageRepository extends JpaRepository<CookImage, Long> {

}
