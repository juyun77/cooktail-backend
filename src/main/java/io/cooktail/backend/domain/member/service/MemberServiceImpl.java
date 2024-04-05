package io.cooktail.backend.domain.member.service;

import io.cooktail.backend.domain.cocktail.domain.CocktailImage;
import io.cooktail.backend.domain.cocktail.service.S3Uploader;
import io.cooktail.backend.domain.member.domain.Member;
import io.cooktail.backend.domain.member.dto.JoinRq;
import io.cooktail.backend.domain.member.dto.MyInfoRq;
import io.cooktail.backend.domain.member.dto.MyInfoRs;
import io.cooktail.backend.domain.member.dto.ProfileRs;
import io.cooktail.backend.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final S3Uploader s3Uploader;

  private static final String DEFAULT_PROFILE_IMAGE_URL = "https://avatar.iran.liara.run/public/"; //https://baconmockup.com/250/250/

  //회원가입 TODO : 이미지 링크 바꾸기
  @Transactional
  @Override
  public void create(JoinRq joinRq)  {

    if (memberRepository.existsByEmail(joinRq.getEmail())) {
      throw new RuntimeException("중복된 이메일입니다.");
    }
    if (memberRepository.existsByNickname(joinRq.getNickname())) {
      throw new RuntimeException("중복된 닉네임입니다.");
    }
    Member member = Member.builder()
        .email(joinRq.getEmail())
        .password(passwordEncoder.encode(joinRq.getPassword()))
        .name(joinRq.getName())
        .nickname(joinRq.getNickname())
        .phone(joinRq.getPhone())
        .birthDate(joinRq.getBirthDate())
        .image(DEFAULT_PROFILE_IMAGE_URL)
        .bio("소개글을 작성해주세요.")
        .build();
    memberRepository.save(member);
  }

  // 로그인 확인
  @Override
  public Member login(String email, String password) {
    return memberRepository.findByEmail(email)
        .filter(member -> passwordEncoder.matches(password, member.getPassword()))
        .orElse(null);
  }

  // 내 정보 조회
  @Override
  public MyInfoRs getMyInfo(long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));
    return MyInfoRs.builder()
        .email(member.getEmail())
        .name(member.getName())
        .nickname(member.getNickname())
        .phone(member.getPhone())
        .birthDate(member.getBirthDate())
        .image(member.getImage())
        .bio(member.getBio())
        .build();
  }

  // 내 정보 수정
  @Transactional
  @Override
  public Long changeMyInfo(long memberId, MyInfoRq myInfoRq, MultipartFile image) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID입니다: " + memberId));

    // 닉네임 변경 여부 확인
    if (!myInfoRq.getNickname().equals(member.getNickname())) {
      // 새로운 닉네임과 기존 닉네임이 다른 경우에만 중복 검사 수행
      if (memberRepository.existsByNickname(myInfoRq.getNickname())) {
        throw new RuntimeException("중복된 닉네임입니다.");
      }
    }

    String oldImageUrl = member.getImage();
    String newImageUrl = null;

    // 새 이미지 업로드 및 연결
    if (image != null && !image.isEmpty()) {
      String dirName = "member";
      try {
        newImageUrl = s3Uploader.uploadFile(dirName, image);
      } catch (Exception e) {
        throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
      }
    }

    // 이미지가 업로드되지 않았거나 빈 이미지일 경우 기존 이미지 URL을 유지하고 업데이트하지 않음
    if (newImageUrl != null) {
      member.update(myInfoRq.getName(), myInfoRq.getNickname(), myInfoRq.getPhone(), newImageUrl, myInfoRq.getBirthDate(), myInfoRq.getBio());
      // 이전 이미지가 기본 이미지가 아닌 경우에만 삭제
      if (!oldImageUrl.equals(DEFAULT_PROFILE_IMAGE_URL)) {
        s3Uploader.deleteFile(oldImageUrl);
      }
    } else {
      // 새 이미지가 없는 경우에는 기존 이미지 URL로만 회원 정보 업데이트
      member.update(myInfoRq.getName(), myInfoRq.getNickname(), myInfoRq.getPhone(), oldImageUrl, myInfoRq.getBirthDate(), myInfoRq.getBio());
    }

    return memberId;
  }


  // 비밀번호 확인
  @Override
  public boolean checkCurrentPassword(long memberId, String currentPassword) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID입니다: " + memberId));
    return passwordEncoder.matches(currentPassword, member.getPassword());
  }

  // 비밀번호 변경
  @Transactional
  @Override
  public void changePassword(long memberId, String newPassword) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID입니다: " + memberId));

    member.update(passwordEncoder.encode(newPassword));
    memberRepository.save(member);
  }

  @Override
  public ProfileRs findById(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원 ID입니다: " + id));
    return ProfileRs.builder()
        .email(member.getEmail())
        .nickname(member.getNickname())
        .birthDate(member.getBirthDate())
        .image(member.getImage())
        .bio(member.getBio())
        .build();
  }
}
