package io.gaboja9.mockstock.domain.members.service;

import io.gaboja9.mockstock.domain.members.dto.request.MemosCreateRequestDto;
import io.gaboja9.mockstock.domain.members.dto.response.MemberInfoDto;
import io.gaboja9.mockstock.domain.members.dto.response.MemoResponseDto;
import io.gaboja9.mockstock.domain.members.entity.Members;
import io.gaboja9.mockstock.domain.members.exception.NotFoundMemberException;
import io.gaboja9.mockstock.domain.members.repository.MembersRepository;
import io.gaboja9.mockstock.domain.orders.service.OrdersService;
import io.gaboja9.mockstock.domain.portfolios.dto.response.PortfoliosResponseDto;
import io.gaboja9.mockstock.domain.portfolios.service.PortfoliosService;
import io.gaboja9.mockstock.domain.ranks.service.RanksService;
import io.gaboja9.mockstock.domain.trades.repository.TradesRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class MembersService {

    private final MembersRepository membersRepository;
    private final TradesRepository tradesRepository;
    private final RanksService ranksService;
    private final PortfoliosService portfoliosService;
    private final OrdersService ordersService;

    @Transactional(readOnly = true)
    public MemberInfoDto getMemberInfoDto(Long memberId, PortfoliosResponseDto portfolios) {
        Members member =
                membersRepository
                        .findById(memberId)
                        .orElseThrow(() -> new NotFoundMemberException(memberId));

        int tradeCnt = tradesRepository.countByMembersId(memberId);
        int ranking = ranksService.getMemberReturnRateRank(memberId);
        int period =
                (int) ChronoUnit.DAYS.between(member.getCreatedAt().toLocalDate(), LocalDate.now());
        int bankruptcyCnt = member.getBankruptcyCnt();

        return MemberInfoDto.builder()
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .totalProfitRate(portfolios.getTotalProfitRate())
                .totalCashBalance(portfolios.getTotalEvaluationAmount() + member.getCashBalance())
                .tradeCnt(tradeCnt)
                .ranking(ranking)
                .period(period + 1)
                .bankruptcyCnt(bankruptcyCnt)
                .memberId(memberId)
                .build();
    }

    @Transactional
    public void processBankruptcy(Long memberId) {

        Members findMember =
                membersRepository
                        .findById(memberId)
                        .orElseThrow(() -> new NotFoundMemberException(memberId));

        portfoliosService.remove(memberId);
        ordersService.remove(memberId);
        findMember.setCashBalance(30_000_000);
        findMember.setTotalInvestedAmount(0);
        findMember.setBankruptcyCnt(findMember.getBankruptcyCnt() + 1);
    }

    @Transactional
    public void createMemo(Long membersId, MemosCreateRequestDto dto) {

        Members findMember =
                membersRepository
                        .findById(membersId)
                        .orElseThrow(() -> new NotFoundMemberException(membersId));

        findMember.setMemo(dto.getMemo());
    }

    @Transactional(readOnly = true)
    public MemoResponseDto getMemo(Long memberId) {

        Members findMember =
                membersRepository
                        .findById(memberId)
                        .orElseThrow(() -> new NotFoundMemberException(memberId));

        return MemoResponseDto.builder().memo(findMember.getMemo()).build();
    }

}
