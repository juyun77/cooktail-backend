package io.cooktail.backend.domain.member.controller;

import io.cooktail.backend.domain.member.domain.Member;
import io.cooktail.backend.domain.member.dto.JoinRq;
import io.cooktail.backend.domain.member.dto.LoginRq;
import io.cooktail.backend.domain.member.dto.LoginRs;
import io.cooktail.backend.domain.member.dto.MyInfoRq;
import io.cooktail.backend.domain.member.dto.MyInfoRs;
import io.cooktail.backend.domain.member.dto.PasswordRq;
import io.cooktail.backend.domain.member.dto.ProfileRs;
import io.cooktail.backend.domain.member.service.MemberService;
import io.cooktail.backend.global.security.TokenProvider;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;

  @PostMapping("/members")
  public ResponseEntity<?> create(@RequestBody JoinRq joinRq) {
      memberService.create(joinRq);
      return ResponseEntity.ok("회원가입 성공");
  }

  //로그인
  @PostMapping("/members/login")
  public ResponseEntity<?> login(@RequestBody LoginRq loginRq) {
    Member member = memberService.login(loginRq.getEmail(), loginRq.getPassword());

    if (member != null) {
      String token = tokenProvider.generateJwtToken(member);
      return ResponseEntity.ok(new LoginRs(token, member.getId()));
    } else {
      return ResponseEntity.status(400).body("로그인 실패 : 이메일이나 비밀번호가 일치하지 않음");
    }
  }

  // 내 정보 조회
  @GetMapping("/members/me")
  public ResponseEntity<MyInfoRs> getMyInfo(@AuthenticationPrincipal String memberId){
    MyInfoRs myInfoRs = memberService.getMyInfo(Long.parseLong(memberId));
    return ResponseEntity.ok(myInfoRs);
  }

  //내 정보 수정
  @PatchMapping("/members/me")
  public Long changeMyInfo(
      @ModelAttribute MyInfoRq myInfoRq,
      @AuthenticationPrincipal String memberId,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return memberService.changeMyInfo(Long.parseLong(memberId), myInfoRq, image);
  }

  // 비밀번호 변경
  @PatchMapping("/members/password")
  public ResponseEntity<String> changePassword(
      @AuthenticationPrincipal String memberId,
      @RequestBody PasswordRq passwordRq) {
    boolean isCurrentPasswordCorrect = memberService.checkCurrentPassword(Long.parseLong(memberId), passwordRq.getCurrentPassword());
    if (isCurrentPasswordCorrect) {
      memberService.changePassword(Long.parseLong(memberId), passwordRq.getNewPassword());
      return ResponseEntity.ok("성공적으로 비밀번호가 변경됐습니다.");
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("현재 비밀번호가 일치하지 않습니다.");
    }
  }

  // id로 조회 (다른 유저 프로필)
  @GetMapping("/members/{id}")
  public ResponseEntity<ProfileRs> findById(@PathVariable("id") Long id){
    ProfileRs profileRs = memberService.findById(id);
    return ResponseEntity.ok(profileRs);
  }

}
