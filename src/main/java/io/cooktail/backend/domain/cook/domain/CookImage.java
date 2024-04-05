package io.cooktail.backend.domain.cook.domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cook_image")
@Getter
@NoArgsConstructor
public class CookImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cook_image_id")
    private long id;

    @Column(name = "image_url", nullable=false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cook_id", nullable=false)
    private Cook cook;

    @Builder
    public CookImage(String imageUrl, Cook cook) {
        this.imageUrl = imageUrl;
        this.cook = cook;
    }
}
