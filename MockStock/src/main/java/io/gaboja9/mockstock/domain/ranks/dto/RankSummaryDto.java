package io.gaboja9.mockstock.domain.ranks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "전체 유저 요약")
public class RankSummaryDto {
    @Schema(description = "총 참여자 수", example = "100")
    private int totalMember;

    @Schema(description = "수익률 달성자 비율", example = "+75.2%")
    private String plusRate;

    @Schema(description = "수익률 손실자 비율", example = "-24.8%")
    private String minusRate;

    @Schema(description = "파산 유저수", example = "10")

    private int bankruptcyMember;
}
