package io.cooktail.backend.domain.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordRq {

  String currentPassword;
  String newPassword;
}
