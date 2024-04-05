package io.cooktail.backend.domain.member.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MyInfoRq {
  private String name;
  private String nickname;
  private String phone;
  private LocalDate birthDate;
  private String bio;
}
