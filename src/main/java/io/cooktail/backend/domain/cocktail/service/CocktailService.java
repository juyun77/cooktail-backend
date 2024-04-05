package io.cooktail.backend.domain.cocktail.service;

import io.cooktail.backend.domain.cocktail.dto.CocktailRq;
import io.cooktail.backend.domain.cocktail.dto.CocktailRs;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CocktailService {

  // 전체 글 조회
  Page<CocktailRs> findAll(Pageable pageable);
  // 게시글 id별 조회
  CocktailRs findById(Long id);
  // 조회수 증가
  int updateView(Long id);
  // 글 작성
  Long createCocktail(Long memberId, CocktailRq cocktailRq, List<String> imageUrls);
  // 글 수정
  Long updateCocktail(Long id, CocktailRq cocktailRq, List<MultipartFile> newImages);
  // 삭제
  void deleteCocktail(Long id);
  // 작성자 검사
  boolean isCocktailAuthor(Long cocktailId, Long memberId);
  // 검색
  Page<CocktailRs> search(Pageable pageable, String keyword);
  // 좋아요
  void addLike(Long cocktailId, Long memberId);
  // 좋아요 해제
  void deleteLike(Long cocktailId, Long memberId);
  // 좋아요 상태 확인
  boolean checkLikeStatus(Long id, Long aLong);
  // 좋아요한 글 조회
  List<CocktailRs> findLikedCocktail(Long memberId);
  // 본인이 작성한 글 조회
  List<CocktailRs> findMemberCocktails(Long memberId);
  // 본인이 작성한 글인지 확인
  boolean isOwnCocktail(Long id, Long aLong);
}
