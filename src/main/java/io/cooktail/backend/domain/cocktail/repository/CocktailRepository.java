package io.cooktail.backend.domain.cocktail.repository;

import io.cooktail.backend.domain.cocktail.domain.Cocktail;
import io.cooktail.backend.domain.member.domain.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CocktailRepository extends JpaRepository<Cocktail, Long> {
  // 조회수 증가
  @Modifying
  @Query("update Cocktail r set r.views = r.views + 1 where r.id = :id")
  int updateView(Long id);

  // 검색
  Page<Cocktail> findByTitleContaining(String keyword, Pageable pageable);

  List<Cocktail> findByMemberOrderByCreatedAtDesc(Member member);
}
