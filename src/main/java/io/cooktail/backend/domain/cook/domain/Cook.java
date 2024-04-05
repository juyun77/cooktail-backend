package io.cooktail.backend.domain.cook.domain;

import io.cooktail.backend.domain.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cook")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cook_id", updatable = false)
    private long id;
    // 제목
    @Column(name = "title", nullable = false)
    private String title;
    // 레시피
    @Column(name = "recipe", nullable = false, columnDefinition = "TEXT")
    private String recipe;
    // 난이도
    @Column(name = "difficulty", nullable = false)
    private int difficulty;
    //이미지
    @OneToMany(mappedBy = "cook", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private final List<CookImage> cookImages = new ArrayList<>();
    // 작성자
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Member member;
    // 작성일
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // 수정일
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    // 조회수
    @Column(name = "views",columnDefinition = "integer default 0", nullable = false)
    private int views;
    // 좋아요수
    @OneToMany(mappedBy = "cook")
    private List<CookLike> likes = new ArrayList<>();

    @Builder
    public Cook(String title, String recipe, int difficulty, Member member) {
        this.title = title;
        this.recipe = recipe;
        this.difficulty = difficulty;
        this.member = member;
    }

    public void update(String title, String recipe, int difficulty) {
        this.title = title;
        this.recipe = recipe;
        this.difficulty = difficulty;
    }

    public int getLikesCount() {
        return likes.size();
    }

}
