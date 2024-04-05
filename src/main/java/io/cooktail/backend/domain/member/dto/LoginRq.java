package io.cooktail.backend.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginRq {

  @NotBlank(message = "이메일이 비어있습니다.")
  @Email(message = "올바른 이메일 주소를 입력해주세요.")
  private String email;

  @NotBlank(message = "비밀번호가 비어있습니다.")
  private String password;
}
