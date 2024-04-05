package io.cooktail.backend.domain.cocktail.controller;

import io.cooktail.backend.domain.cocktail.dto.CocktailRq;
import io.cooktail.backend.domain.cocktail.dto.CocktailRs;
import io.cooktail.backend.domain.cocktail.service.CocktailService;
import io.cooktail.backend.domain.cocktail.service.S3Uploader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CocktailController {

  private final CocktailService service;
  private final S3Uploader s3Uploader;

  // 모든 글 조회, 검색
  @GetMapping("/cocktails")
  public Page<CocktailRs> getAllCocktail(
      @PageableDefault(page = 0, size = 8, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
      @RequestParam(required = false) String keyword) {

    if ("like,desc".equals(pageable.getSort())) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "likes.size"));
    }

    if (keyword == null) {
      return service.findAll(pageable);
    }
    else return service.search(pageable,keyword);
  }

  // id로 조회
  @GetMapping("/cocktails/{id}")
  public CocktailRs getCocktailById(@PathVariable Long id) {
    service.updateView(id);
    return service.findById(id);
  }

  // 작성
  @PostMapping("/cocktails")
  public ResponseEntity<Long> createCocktail(
      @AuthenticationPrincipal String memberId,
      @ModelAttribute CocktailRq cocktailRq,
      @RequestPart(value = "images") List<MultipartFile> images) throws IOException {
    String dirName = "cocktail";
    List<String> imageUrls = s3Uploader.uploadFiles(dirName, images);
    Long cocktailId = service.createCocktail(Long.parseLong(memberId),cocktailRq,imageUrls);
    return ResponseEntity.ok(cocktailId);
  }

  // 수정
  @PutMapping ("/cocktails/{id}")
  public Long updateCocktail(
      @PathVariable Long id,
      @AuthenticationPrincipal String memberId,
      @ModelAttribute CocktailRq cocktailRq,
      @RequestPart(value = "images") List<MultipartFile> images) throws IOException {
    if (!service.isCocktailAuthor(id, Long.parseLong(memberId))) {
      throw new AccessDeniedException("이 글을 수정할 권한이 없습니다.");
    }
    return service.updateCocktail(id, cocktailRq, images);
  }
  // 삭제
  @DeleteMapping("/cocktails/{id}")
  public String deleteCocktail(
      @PathVariable Long id,
      @AuthenticationPrincipal String memberId) throws AccessDeniedException {
    if (!service.isCocktailAuthor(id, Long.parseLong(memberId))) {
      throw new AccessDeniedException("이 글을 삭제할 권한이 없습니다.");
    }
    service.deleteCocktail(id);
    return "성공적으로 삭제되었습니다";
  }

  // 좋아요
  @PostMapping("/cocktails/like/{id}")
  public Long addLike(
      @PathVariable Long id,
      @AuthenticationPrincipal String memberId) {
      service.addLike(id, Long.valueOf(memberId));
      return id;
  }


  // 좋아요 해제
  @DeleteMapping("/cocktails/like/{id}")
  public Long deleteLike(
      @PathVariable Long id,
      @AuthenticationPrincipal String memberId){
    service.deleteLike(id, Long.valueOf(memberId));
    return id;
  }

  // 좋아요 상태 확인
  @GetMapping("/cocktails/like/{id}/status")
  public boolean checkLikeStatus(
      @PathVariable Long id,
      @AuthenticationPrincipal String memberId) {
    return service.checkLikeStatus(id, Long.valueOf(memberId));
  }


  // 좋아요한 글 조회
  @GetMapping("/cocktails/like")
  public ResponseEntity<List<CocktailRs>> getLikedCocktail(@AuthenticationPrincipal String memberId) {
    List<CocktailRs> cocktailRs = service.findLikedCocktail(Long.valueOf(memberId));
    return ResponseEntity.ok(cocktailRs);
  }

  // 본인이 작성한 글 조회
  @GetMapping("/cocktails/me")
  public ResponseEntity<List<CocktailRs>> getMemberCocktails(@AuthenticationPrincipal String memberId) {
    List<CocktailRs> memberCocktails = service.findMemberCocktails(Long.valueOf(memberId));
    return ResponseEntity.ok(memberCocktails);
  }

  // 본인이 작성한 글인지 확인
  @GetMapping("/cocktails/{id}/isOwn")
  public boolean checkIsOwnCocktail(
      @PathVariable Long id,
      @AuthenticationPrincipal String memberId) {
    return service.isOwnCocktail(id, Long.valueOf(memberId));
  }


}
