package io.gaboja9.mockstock.domain.members.service;

import io.gaboja9.mockstock.domain.members.entity.Members;
import io.gaboja9.mockstock.domain.members.repository.MembersRepository;
import io.gaboja9.mockstock.domain.portfolios.dto.response.PortfoliosResponseDto;
import io.gaboja9.mockstock.domain.portfolios.service.PortfoliosService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyProfitRateScheduler {
    private final MembersRepository membersRepository;
    private final PortfoliosService portfoliosService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateYesterdayProfitRate() {

        List<Members> allMembers = membersRepository.findAll();

        for (Members member : allMembers) {

            PortfoliosResponseDto dto = portfoliosService.getPortfolios(member.getId());
            double profitRate = dto.getTotalProfitRate();

            member.setYesterdayProfitRate(profitRate);
        }

        membersRepository.saveAll(allMembers);
    }
}
