package io.cooktail.backend.domain.cocktail.service;

import io.cooktail.backend.domain.cocktail.domain.Cocktail;
import io.cooktail.backend.domain.cocktail.domain.CocktailImage;
import io.cooktail.backend.domain.cocktail.domain.CocktailLike;
import io.cooktail.backend.domain.cocktail.dto.CocktailRq;
import io.cooktail.backend.domain.cocktail.dto.CocktailRs;
import io.cooktail.backend.domain.cocktail.repository.CocktailImageRepository;
import io.cooktail.backend.domain.cocktail.repository.CocktailRepository;
import io.cooktail.backend.domain.cocktail.repository.CocktailLikeRepository;
import io.cooktail.backend.domain.member.domain.Member;
import io.cooktail.backend.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CocktailServiceImpl implements CocktailService{

  private final CocktailRepository cocktailRepository;
  private final CocktailImageRepository cocktailImageRepository;
  private final MemberRepository memberRepository;
  private final CocktailLikeRepository cocktailLikeRepository;
  private final S3Uploader s3Uploader;

  // 전체 글 조회
  @Override
  public Page<CocktailRs> findAll(Pageable pageable) {
    Page<Cocktail> cocktailPage = cocktailRepository.findAll(pageable);

    Page<CocktailRs> cocktailRs = cocktailPage.map(cocktail -> CocktailRs.builder()
        .cocktail(cocktail)
        .images(cocktail.getCocktailImages().stream()
            .map(CocktailImage::getImageUrl)
            .collect(Collectors.toList()))
        .build());

    return cocktailRs;
  }

  // 게시글 id별 조회
  @Override
  public CocktailRs findById(Long id) {
    Cocktail cocktail = cocktailRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id));

    CocktailRs cocktailRs = CocktailRs.builder()
        .cocktail(cocktail)
        .images(cocktail.getCocktailImages().stream()
            .map(CocktailImage::getImageUrl)
            .collect(Collectors.toList()))
        .build();

    return cocktailRs;
  }

  // 조회수 증가
  @Override
  @Transactional
  public int updateView(Long id){
    return cocktailRepository.updateView(id);
  }

  // 글 작성
  @Override
  @Transactional
  public Long createCocktail(Long memberId, CocktailRq cocktailRq, List<String> imageUrls) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

    Cocktail cocktail = cocktailRepository.save(Cocktail.builder()
        .title(cocktailRq.getTitle())
        .description(cocktailRq.getDescription())
        .ingredient(cocktailRq.getIngredient())
        .recipe(cocktailRq.getRecipe())
        .abv(cocktailRq.getAbv())
        .member(member)
        .build());

    for (String imageUrl : imageUrls) {
      cocktailImageRepository.save(CocktailImage.builder()
          .imageUrl(imageUrl)
          .cocktail(cocktail)
          .build());
    }
    return cocktail.getId();
  }

  // 글 수정
  @Override
  @Transactional
  public Long updateCocktail(Long id, CocktailRq cocktailRq, List<MultipartFile> newImages) {
    Cocktail cocktail = cocktailRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id));

    cocktail.update(cocktailRq.getTitle(), cocktailRq.getDescription(), cocktailRq.getIngredient(), cocktailRq.getRecipe(), cocktailRq.getAbv());
    cocktailRepository.save(cocktail);

    // 기존 이미지 삭제
    List<CocktailImage> existingImages = cocktail.getCocktailImages();
    for (CocktailImage image : existingImages) {
      s3Uploader.deleteFile(image.getImageUrl());
      cocktailImageRepository.delete(image);
    }

    // 새 이미지 업로드 및 연결
    String dirName = "cocktail";
    List<String> newImageUrls = s3Uploader.uploadFiles(dirName, newImages);
    for (String imageUrl : newImageUrls) {
      cocktailImageRepository.save(CocktailImage.builder()
          .imageUrl(imageUrl)
          .cocktail(cocktail)
          .build());
    }
    return id;
  }

  // 삭제
  @Transactional
  @Override
  public void deleteCocktail(Long id) {
    Cocktail cocktail = cocktailRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id));

    // 연결된 이미지 삭제
    List<CocktailImage> cocktailImages = cocktail.getCocktailImages();
    for (CocktailImage image : cocktailImages) {
      s3Uploader.deleteFile(image.getImageUrl());
      cocktailImageRepository.delete(image);
    }

    // 글 삭제
    cocktailRepository.delete(cocktail);
  }

  // 작성자 검사
  @Override
  public boolean isCocktailAuthor(Long cocktailId, Long memberId) {
    Optional<Cocktail> optionalCocktail = cocktailRepository.findById(cocktailId);
    return optionalCocktail.map(cocktail -> cocktail.getMember().getId().equals(memberId)).orElse(false);
  }

  // 검색
  @Override
  public Page<CocktailRs> search(Pageable pageable, String keyword) {
    Page<Cocktail> cocktailPage = cocktailRepository.findByTitleContaining(keyword,pageable);

    Page<CocktailRs> cocktailRs = cocktailPage.map(cocktail -> CocktailRs.builder()
        .cocktail(cocktail)
        .images(cocktail.getCocktailImages().stream()
            .map(CocktailImage::getImageUrl)
            .collect(Collectors.toList()))
        .build());

    return cocktailRs;
  }

  // 좋아요
  @Override
  @Transactional
  public void addLike(Long cocktailId, Long memberId) {
    Cocktail cocktail = cocktailRepository.findById(cocktailId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + cocktailId));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

    if (cocktail.getMember().equals(member)) {
      throw new IllegalArgumentException("자신이 작성한 글에는 좋아요를 누를 수 없습니다.");
    }

    if (cocktailLikeRepository.existsByMemberAndCocktail(member, cocktail)) {
      throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
    }

    cocktailLikeRepository.save(CocktailLike.builder()
        .member(member)
        .cocktail(cocktail)
        .build());
  }

  // 좋아요 해제
  @Override
  @Transactional
  public void deleteLike(Long cocktailId, Long memberId) {
    Cocktail cocktail = cocktailRepository.findById(cocktailId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + cocktailId));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

    CocktailLike cocktailLike = cocktailLikeRepository.findByMemberAndCocktail(member, cocktail)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 좋아요를 찾을 수 없습니다."));
    cocktailLikeRepository.delete(cocktailLike);
  }

  @Override
  public boolean checkLikeStatus(Long cocktailId, Long memberId) {
    Cocktail cocktail = cocktailRepository.findById(cocktailId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + cocktailId));
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

    return cocktailLikeRepository.existsByMemberAndCocktail(member, cocktail);
  }


  // 좋아요한 글 조회
  @Override
  public List<CocktailRs> findLikedCocktail(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

    List<CocktailLike> likedCocktailLikes = cocktailLikeRepository.findByMember(member);

    List<CocktailRs> likedCocktails = likedCocktailLikes.stream()
        .map(CocktailLike::getCocktail)
        .sorted(Comparator.comparing(Cocktail::getCreatedAt).reversed()) // 최신순 정렬
        .map(cocktail -> CocktailRs.builder()
            .cocktail(cocktail)
            .images(cocktail.getCocktailImages().stream()
                .map(CocktailImage::getImageUrl)
                .collect(Collectors.toList()))
            .build())
        .collect(Collectors.toList());

    return likedCocktails;
  }

  // 본인이 작성한 글 조회
  @Override
  public List<CocktailRs> findMemberCocktails(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new NoSuchElementException("해당 ID에 매칭되는 Member를 찾을 수 없습니다: " + memberId));

    List<Cocktail> memberCocktails = cocktailRepository.findByMemberOrderByCreatedAtDesc(member);

    return memberCocktails.stream()
        .map(cocktail -> CocktailRs.builder()
            .cocktail(cocktail)
            .images(cocktail.getCocktailImages().stream()
                .map(CocktailImage::getImageUrl)
                .collect(Collectors.toList()))
            .build())
        .collect(Collectors.toList());
  }
  //본인이 작성한 글인지 확인
  @Override
  public boolean isOwnCocktail(Long cocktailId, Long memberId) {
    Optional<Cocktail> optionalCocktail = cocktailRepository.findById(cocktailId);
    Optional<Member> optionalMember = memberRepository.findById(memberId);

    if (optionalCocktail.isPresent() && optionalMember.isPresent()) {
      Cocktail cocktail = optionalCocktail.get();
      Member member = optionalMember.get();

      return cocktail.getMember().getId().equals(member.getId());
    }

    // 해당하는 Cocktail이나 Member가 없다면 false를 반환
    return false;
  }


}
