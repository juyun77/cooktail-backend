package io.cooktail.backend.domain.cook.repository;

import io.cooktail.backend.domain.cook.domain.Cook;
import io.cooktail.backend.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CookRepository extends JpaRepository<Cook, Long> {
    // 조회수 증가
    @Modifying
    @Query("update Cook c set c.views = c.views + 1 where c.id = :id")
    int updateView(Long id);

    // 검색
    Page<Cook> findByTitleContaining(String keyword, Pageable pageable);

    List<Cook> findByMemberOrderByCreatedAtDesc(Member member);
}
