package io.cooktail.backend.domain.cook.repository;

import io.cooktail.backend.domain.cook.domain.Cook;
import io.cooktail.backend.domain.cook.domain.CookLike;
import io.cooktail.backend.domain.member.domain.Member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookLikeRepository extends JpaRepository<CookLike, Long> {
    Boolean existsByMemberAndCook(Member member, Cook cook);

    Optional<CookLike> findByMemberAndCook(Member member, Cook cook);

    List<CookLike> findByMember(Member member);
}
