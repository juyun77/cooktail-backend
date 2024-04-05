package io.cooktail.backend.domain.cook.domain; // Updated package name

import io.cooktail.backend.domain.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cook_like") // Updated table name
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CookLike { // Updated entity name

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", updatable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cook_id") // Updated column name
    private Cook cook; // Updated entity name

    @Builder
    public CookLike(Member member, Cook cook) { // Updated entity name
        this.member = member;
        this.cook = cook; // Updated entity name
    }
}
