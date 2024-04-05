package io.cooktail.backend.domain.cook.service;

import io.cooktail.backend.domain.cocktail.domain.Cocktail;
import io.cooktail.backend.domain.cook.domain.Cook;
import io.cooktail.backend.domain.cook.domain.CookImage;
import io.cooktail.backend.domain.cook.domain.CookLike;
import io.cooktail.backend.domain.cook.dto.CookRq;
import io.cooktail.backend.domain.cook.dto.CookRs;
import io.cooktail.backend.domain.cook.repository.CookImageRepository;
import io.cooktail.backend.domain.cook.repository.CookRepository;
import io.cooktail.backend.domain.cook.repository.CookLikeRepository;
import io.cooktail.backend.domain.member.domain.Member;
import io.cooktail.backend.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.cooktail.backend.domain.cocktail.service.S3Uploader;

@Service
@RequiredArgsConstructor
public class CookServiceImpl implements CookService {

    private final CookRepository cookRepository;
    private final CookImageRepository cookImageRepository;
    private final MemberRepository memberRepository;
    private final CookLikeRepository cookLikeRepository;
    private final S3Uploader s3Uploader;

    // 전체 글 조회
    @Override
    public Page<CookRs> findAll(Pageable pageable) {
        Page<Cook> cookPage = cookRepository.findAll(pageable);

        Page<CookRs> cookRs = cookPage.map(cook -> CookRs.builder()
                .cook(cook)
                .images(cook.getCookImages().stream()
                        .map(CookImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build());

        return cookRs;
    }

    // 게시글 id별 조회
    @Override
    public CookRs findById(Long id) {
        Cook cook = cookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id));

        CookRs cookRs = CookRs.builder()
                .cook(cook)
                .images(cook.getCookImages().stream()
                        .map(CookImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();

        return cookRs;
    }

    // 조회수 증가
    @Override
    @Transactional
    public int updateView(Long id){
        return cookRepository.updateView(id);
    }

    // 글 작성
    @Override
    @Transactional
    public Long createCook(Long memberId, CookRq cookRq, List<String> imageUrls) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

        Cook cook = cookRepository.save(Cook.builder()
                .title(cookRq.getTitle())
                .recipe(cookRq.getRecipe())
                .difficulty(cookRq.getDifficulty())
                .member(member)
                .build());

        for (String imageUrl : imageUrls) {
            cookImageRepository.save(CookImage.builder()
                    .imageUrl(imageUrl)
                    .cook(cook)
                    .build());
        }
        return cook.getId();
    }

    // 글 수정
    @Override
    @Transactional
    public Long updateCook(Long id, CookRq cookRq, List<MultipartFile> newImages) {
        Cook cook = cookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id));

        cook.update(cookRq.getTitle(), cookRq.getRecipe(), cookRq.getDifficulty());
        cookRepository.save(cook);

        // 기존 이미지 삭제
        List<CookImage> existingImages = cook.getCookImages();
        for (CookImage image : existingImages) {
            s3Uploader.deleteFile(image.getImageUrl());
            cookImageRepository.delete(image);
        }

        // 새 이미지 업로드 및 연결
        String dirName = "cook";
        List<String> newImageUrls = s3Uploader.uploadFiles(dirName, newImages);
        for (String imageUrl : newImageUrls) {
            cookImageRepository.save(CookImage.builder()
                    .imageUrl(imageUrl)
                    .cook(cook)
                    .build());
        }
        return id;
    }

    // 삭제
    @Transactional
    @Override
    public void deleteCook(Long id) {
        Cook cook = cookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id));

        // 연결된 이미지 삭제
        List<CookImage> cookImages = cook.getCookImages();
        for (CookImage image : cookImages) {
            s3Uploader.deleteFile(image.getImageUrl());
            cookImageRepository.delete(image);
        }

        // 글 삭제
        cookRepository.delete(cook);
    }

    // 작성자 검사
    @Override
    public boolean isCookAuthor(Long cookId, Long memberId) {
        Optional<Cook> optionalCook = cookRepository.findById(cookId);
        return optionalCook.map(cook -> cook.getMember().getId().equals(memberId)).orElse(false);
    }

    // 검색
    @Override
    public Page<CookRs> search(Pageable pageable, String keyword) {
        Page<Cook> cookPage = cookRepository.findByTitleContaining(keyword,pageable);

        Page<CookRs> cookRs = cookPage.map(cook -> CookRs.builder()
                .cook(cook)
                .images(cook.getCookImages().stream()
                        .map(CookImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build());

        return cookRs;
    }

    // 좋아요
    @Override
    @Transactional
    public void addLike(Long cookId, Long memberId) {
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + cookId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

        if (cook.getMember().equals(member)) {
            throw new IllegalArgumentException("자신이 작성한 글에는 좋아요를 누를 수 없습니다.");
        }

        if (cookLikeRepository.existsByMemberAndCook(member, cook)) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        cookLikeRepository.save(CookLike.builder()
                .member(member)
                .cook(cook)
                .build());
    }

    // 좋아요 해제
    @Override
    @Transactional
    public void deleteLike(Long cookId, Long memberId) {
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + cookId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

        CookLike cookLike = cookLikeRepository.findByMemberAndCook(member, cook)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 좋아요를 찾을 수 없습니다."));
        cookLikeRepository.delete(cookLike);
    }

    // 좋아요 글 조회
    @Override
    public List<CookRs> findLikedCook(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

        List<CookLike> likedCookLikes = cookLikeRepository.findByMember(member);

        List<CookRs> likedCooks = likedCookLikes.stream()
                .map(CookLike::getCook)
                .sorted(Comparator.comparing(Cook::getCreatedAt).reversed()) // 최신순 정렬
                .map(cook -> CookRs.builder()
                        .cook(cook)
                        .images(cook.getCookImages().stream()
                                .map(CookImage::getImageUrl)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return likedCooks;
    }

    // 본인이 작성한 레시피 조회
    @Override
    public List<CookRs> findMemberCooks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

        List<Cook> memberCooks = cookRepository.findByMemberOrderByCreatedAtDesc(member);

        return memberCooks.stream()
                .map(cook -> CookRs.builder()
                .cook(cook)
                .images(cook.getCookImages().stream()
                        .map(CookImage::getImageUrl)
                        .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkLikeStatus(Long cookId, Long memberId) {
        Cook cook = cookRepository.findById(cookId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + cookId));
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

        return cookLikeRepository.existsByMemberAndCook(member, cook);
    }

    @Override
    public boolean isOwnCook(Long cookId, Long memberId) {
        Optional<Cook> optionalCook = cookRepository.findById(cookId);
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if (optionalCook.isPresent() && optionalMember.isPresent()) {
            Cook cook = optionalCook.get();
            Member member = optionalMember.get();

            return cook.getMember().getId().equals(member.getId());
        }

        // 해당하는 Cook이나 Member가 없다면 false를 반환
        return false;
    }
}
