package io.cooktail.backend.domain.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "nickname", nullable = false, unique = true)
  private String nickname;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "image", nullable=false)
  private String image;

  @Past
  @Column(name = "birth_date", nullable=false)
  private LocalDate birthDate;

  @Column(name = "bio", columnDefinition = "TEXT")
  private String bio;

  @Builder
  public Member(String email, String password, String name, String nickname, String phone, String image, LocalDate birthDate, String bio) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.phone = phone;
    this.image = image;
    this.birthDate = birthDate;
    this.bio = bio;
  }

  public void update(String name, String nickname, String phone, String image, LocalDate birthDate,
      String bio) {
    this.name = name;
    this.nickname = nickname;
    this.phone = phone;
    this.image = image;
    this.birthDate = birthDate;
    this.bio = bio;
  }
  public void update(String password) {
    this.password = password;
  }

}
