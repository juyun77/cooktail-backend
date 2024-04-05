package io.cooktail.backend.domain.member.repository;

import io.cooktail.backend.domain.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail (String email);
  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

}
