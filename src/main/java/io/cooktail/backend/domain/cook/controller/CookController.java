package io.cooktail.backend.domain.cook.controller;

import io.cooktail.backend.domain.cook.dto.CookRq;
import io.cooktail.backend.domain.cook.dto.CookRs;
import io.cooktail.backend.domain.cook.service.CookService;
import io.cooktail.backend.domain.cocktail.service.S3Uploader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class CookController {

    private final CookService service;
    private final S3Uploader s3Uploader;

    // 모든 글 조회, 검색
    @GetMapping("/cooks")
    public Page<CookRs> getAllCook(
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
    @GetMapping("/cooks/{id}")
    public CookRs getCookById(@PathVariable Long id) {
        service.updateView(id);
        return service.findById(id);
    }

    // 작성
    @PostMapping("/cooks")
    public Long createCook(
            @AuthenticationPrincipal String memberId,
            @ModelAttribute CookRq cookRq,
            @RequestPart(value = "images") List<MultipartFile> images) throws IOException {
        String dirName = "cook";
        List<String> imageUrls = s3Uploader.uploadFiles(dirName, images);
        return service.createCook(Long.parseLong(memberId), cookRq, imageUrls);
    }

    // 수정
    @PutMapping ("/cooks/{id}")
    public Long updateCook(
            @PathVariable Long id,
            @AuthenticationPrincipal String memberId,
            @ModelAttribute CookRq cookRq,
            @RequestPart(value = "images") List<MultipartFile> images) throws IOException {
        if (!service.isCookAuthor(id, Long.parseLong(memberId))) {
            throw new AccessDeniedException("이 글을 수정할 권한이 없습니다.");
        }
        return service.updateCook(id, cookRq, images);
    }
    // 삭제
    @DeleteMapping("/cooks/{id}")
    public String deleteCook(
            @PathVariable Long id,
            @AuthenticationPrincipal String memberId) throws AccessDeniedException {
        if (!service.isCookAuthor(id, Long.parseLong(memberId))) {
            throw new AccessDeniedException("이 글을 삭제할 권한이 없습니다.");
        }
        service.deleteCook(id);
        return "성공적으로 삭제되었습니다";
    }

    // 좋아요
    @PostMapping("/cooks/like/{id}")
    public Long addLike(
            @PathVariable Long id,
            @AuthenticationPrincipal String memberId) {
        service.addLike(id, Long.valueOf(memberId));
        return id;
    }

    // 좋아요 해제
    @DeleteMapping("/cooks/like/{id}")
    public Long deleteLike(
            @PathVariable Long id,
            @AuthenticationPrincipal String memberId){
        service.deleteLike(id, Long.valueOf(memberId));
        return id;
    }

    // 좋아요 상태 확인
    @GetMapping("/cooks/like/{id}/status")
    public boolean checkLikeStatus(
        @PathVariable Long id,
        @AuthenticationPrincipal String memberId) {
        return service.checkLikeStatus(id, Long.valueOf(memberId));
    }

    // 좋아요한 레시피 조회
    @GetMapping("/cooks/like")
    public List<CookRs> getLikedCook(@AuthenticationPrincipal String memberId) {
        return service.findLikedCook(Long.valueOf(memberId));
    }

    // 본인이 작성한 레시피 조회
    @GetMapping("/cooks/me")
    public ResponseEntity<List<CookRs>> getMemberCocktails(@AuthenticationPrincipal String memberId) {
        List<CookRs> memberCooks = service.findMemberCooks(Long.valueOf(memberId));
        return ResponseEntity.ok(memberCooks);
    }

    // 본인이 작성한 글인지 확인
    @GetMapping("/cooks/{id}/isOwn")
    public boolean checkIsOwnCook(
        @PathVariable Long id,
        @AuthenticationPrincipal String memberId) {
        return service.isOwnCook(id, Long.valueOf(memberId));
    }

}
