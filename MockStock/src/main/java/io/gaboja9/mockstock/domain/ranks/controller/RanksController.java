package io.gaboja9.mockstock.domain.ranks.controller;

import io.gaboja9.mockstock.domain.auth.dto.MembersDetails;
import io.gaboja9.mockstock.domain.members.repository.MembersRepository;
import io.gaboja9.mockstock.domain.ranks.dto.RankSummaryDto;
import io.gaboja9.mockstock.domain.ranks.dto.RankingRequest;
import io.gaboja9.mockstock.domain.ranks.dto.RankingResponse;
import io.gaboja9.mockstock.domain.ranks.entity.RanksType;
import io.gaboja9.mockstock.domain.ranks.service.RanksService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor
public class RanksController implements RanksControllerSpec {

    private final RanksService ranksService;

    @GetMapping
    public ResponseEntity<RankingResponse> getRankingWithPagination(
            @AuthenticationPrincipal MembersDetails membersDetails,
            @RequestParam RanksType ranksType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        RankingRequest request = RankingRequest.of(ranksType, page, size);
        RankingResponse response =
                ranksService.getRankingWithPagination(membersDetails.getId(), request);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> updateRanking() {

        ranksService.updateAndCacheRanks();

        return ResponseEntity.ok("랭킹 갱신 성공");
    }

    @GetMapping("/summary")
    public ResponseEntity<RankSummaryDto> summary() {
        RankSummaryDto summaryDto = ranksService.getRankSummary();
        return ResponseEntity.ok(summaryDto);
    }
}
