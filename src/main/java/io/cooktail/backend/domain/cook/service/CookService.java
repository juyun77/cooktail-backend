package io.cooktail.backend.domain.cook.service;

import io.cooktail.backend.domain.cook.dto.CookRq;
import io.cooktail.backend.domain.cook.dto.CookRs;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CookService {

    // 전체 글 조회
    Page<CookRs> findAll(Pageable pageable);
    // 게시글 id별 조회
    CookRs findById(Long id);
    // 조회수 증가
    int updateView(Long id);
    // 글 작성
    Long createCook(Long memberId, CookRq cookRq, List<String> imageUrls);
    // 글 수정
    Long updateCook(Long id, CookRq cookRq, List<MultipartFile> newImages);
    // 삭제
    void deleteCook(Long id);
    // 작성자 검사
    boolean isCookAuthor(Long cookId, Long memberId);
    // 검색
    Page<CookRs> search(Pageable pageable, String keyword);
    // 좋아요
    void addLike(Long cookId, Long memberId);
    // 좋아요 해제
    void deleteLike(Long cookId, Long memberId);
    // 좋아요 글 조회
    List<CookRs> findLikedCook(Long memberId);
    // 본인이 작성한 레시피 조회
    List<CookRs> findMemberCooks(Long memberId);

    // 좋아요 상태 확인
    boolean checkLikeStatus(Long id, Long aLong);
    // 본인이 작성한 글인지 확인
    boolean isOwnCook(Long id, Long aLong);
}
