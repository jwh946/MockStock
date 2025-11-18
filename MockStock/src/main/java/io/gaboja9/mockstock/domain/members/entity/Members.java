package io.gaboja9.mockstock.domain.members.entity;

import io.gaboja9.mockstock.domain.members.enums.Role;
import io.gaboja9.mockstock.global.common.BaseEntity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Members extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;

    private String provider;

    private String profileImage;

    @Setter private int cashBalance = 30_000_000;

    @Setter private int totalInvestedAmount = 0;

    @Setter private int bankruptcyCnt;

    @Setter private String memo;

    @Column(nullable = true)
    @Setter
    private String password;

    @Setter
    private double yesterdayProfitRate = 0.0;

    @Builder
    public Members(
            String email,
            String nickname,
            Role role,
            String provider,
            String profileImage,
            int cashBalance,
            int bankruptcyCnt,
            String password) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.provider = provider;
        this.profileImage = profileImage;
        this.cashBalance = cashBalance;
        this.bankruptcyCnt = bankruptcyCnt;
        this.password = password;
    }

    // 테스트용 생성자
    public Members(
            Long id,
            String email,
            String nickname,
            String provider,
            String profileImage,
            int cashBalance,
            int bankruptcyCnt,
            LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.profileImage = profileImage;
        this.cashBalance = cashBalance;
        this.bankruptcyCnt = bankruptcyCnt;
        this.setCreatedAt(createdAt);
    }
}
