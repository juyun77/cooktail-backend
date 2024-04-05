package io.cooktail.backend.domain.member.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileRs {
  private String email;
  private String nickname;
  private LocalDate birthDate;
  private String image;
  private String bio;

  @Builder
  public ProfileRs(String email, String nickname, LocalDate birthDate, String image, String bio) {
    this.email = email;
    this.nickname = nickname;
    this.birthDate = birthDate;
    this.image = image;
    this.bio = bio;
  }
}
