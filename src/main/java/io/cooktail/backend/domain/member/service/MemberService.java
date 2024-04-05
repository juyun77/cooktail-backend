package io.cooktail.backend.domain.member.service;

import io.cooktail.backend.domain.member.domain.Member;
import io.cooktail.backend.domain.member.dto.JoinRq;
import io.cooktail.backend.domain.member.dto.MyInfoRq;
import io.cooktail.backend.domain.member.dto.MyInfoRs;
import io.cooktail.backend.domain.member.dto.ProfileRs;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

  // 회원가입
  void create(JoinRq joinRq);

  // 로그인 확인
  Member login(String email, String password);

  // 내 정보 조회
  MyInfoRs getMyInfo(long memberId);

  // 내 정보 수정
  Long changeMyInfo(long memberId, MyInfoRq myInfoRq, MultipartFile image);

  // 비밀번호 확인
  boolean checkCurrentPassword(long memberId, String currentPassword);

  // 비밀번호 변경
  void changePassword(long memberId, String newPassword);
  // id로 조회
  ProfileRs findById(Long id);
}
