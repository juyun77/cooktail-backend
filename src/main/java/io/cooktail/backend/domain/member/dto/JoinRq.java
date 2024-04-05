package io.cooktail.backend.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JoinRq {

  @NotBlank(message = "이메일이 비어있습니다.")
  @Email(message = "올바른 이메일 주소를 입력해주세요.")
  private String email;

  @NotBlank(message = "비밀번호가 비어있습니다.")
  private String password;

  @NotBlank(message = "이름이 비어있습니다.")
  private String name;

  @NotBlank(message = "별명이 비어있습니다.")
  private String nickname;

  @NotBlank(message = "전화번호가 비어있습니다.")
  @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 10자리 또는 11자리의 숫자로 입력해주세요.")
  private String phone;

  @Past(message = "생년월일은 과거 날짜만 허용됩니다.")
  @NotBlank(message = "생년월일이 비어있습니다.")
  private LocalDate birthDate;

  @Builder
  public JoinRq(String email, String password, String name, String nickname, String phone,
      LocalDate birthDate) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.phone = phone;
    this.birthDate = birthDate;
  }
}
