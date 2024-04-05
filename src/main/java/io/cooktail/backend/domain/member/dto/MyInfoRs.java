package io.cooktail.backend.domain.member.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyInfoRs {

  private String email;
  private String name;
  private String nickname;
  private String phone;
  private LocalDate birthDate;
  private String image;
  private String bio;

  @Builder
  public MyInfoRs(String email, String name, String nickname, String phone, LocalDate birthDate,
      String image, String bio) {
    this.email = email;
    this.name = name;
    this.nickname = nickname;
    this.phone = phone;
    this.birthDate = birthDate;
    this.image = image;
    this.bio = bio;
  }
}
