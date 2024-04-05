package io.cooktail.backend.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRs {
  private String token;
  private Long id;

  @Builder
  public LoginRs(String token, Long id) {
    this.token = token;
    this.id = id;
  }
}
