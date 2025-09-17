package io.gaboja9.mockstock.domain.orders.service;

import io.gaboja9.mockstock.domain.members.entity.Members;
import io.gaboja9.mockstock.domain.notifications.service.NotificationsService;
import io.gaboja9.mockstock.domain.orders.entity.OrderStatus;
import io.gaboja9.mockstock.domain.orders.entity.Orders;
import io.gaboja9.mockstock.domain.orders.exception.NotFoundOrderException;
import io.gaboja9.mockstock.domain.orders.repository.OrdersRepository;
import io.gaboja9.mockstock.domain.portfolios.entity.Portfolios;
import io.gaboja9.mockstock.domain.portfolios.repository.PortfoliosRepository;
import io.gaboja9.mockstock.domain.portfolios.service.PortfoliosService;
import io.gaboja9.mockstock.domain.trades.entity.TradeType;
import io.gaboja9.mockstock.domain.trades.entity.Trades;
import io.gaboja9.mockstock.domain.trades.repository.TradesRepository;
import io.gaboja9.mockstock.global.websocket.HantuWebSocketHandler;
import io.gaboja9.mockstock.global.websocket.dto.StockPriceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class LimitOrdersExecutor {

    private final OrdersRepository ordersRepository;
    private final HantuWebSocketHandler hantuWebSocketHandler;
    private final TradesRepository tradesRepository;
    private final PortfoliosService portfoliosService;
    private final NotificationsService notificationsService;
    private final PortfoliosRepository portfoliosRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> memberLocks = new ConcurrentHashMap<>();

    /**
     * 사용자별 락 획득
     */
    private ReentrantLock getMemberLock(Long memberId) {
        return memberLocks.computeIfAbsent(memberId, k -> new ReentrantLock());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processIndividualOrder(Orders order) {
        try {
            processOrderInternal(order);
        } catch (Exception e) {
            log.error("주문 처리 중 오류 발생. orderId={}, error={}", order.getId(), e.getMessage(), e);
        }
    }

    private void processOrderInternal(Orders order) {
        Orders currentOrder = ordersRepository.findByIdWithMember(order.getId())
                .orElseThrow(NotFoundOrderException::new);

        if (currentOrder.getStatus() != OrderStatus.PENDING) {
            log.debug("이미 처리된 주문입니다. orderId={}, status={}",
                    order.getId(), currentOrder.getStatus());
            return;
        }

        StockPriceDto price = hantuWebSocketHandler.getLatestPrice(order.getStockCode());
        if (price == null) {
            log.warn("실시간 가격 정보 없음. orderId={}, stockCode={}",
                    order.getId(), order.getStockCode());
            return;
        }

        int currentPrice = price.getCurrentPrice();

        if (!shouldExecuteOrder(currentOrder, currentPrice)) {
            log.debug("체결 조건 불만족. orderId={}, currentPrice={}, limitPrice={}, type={}",
                    order.getId(), currentPrice, order.getPrice(), order.getTradeType());
            return;
        }

        executeOrderWithLock(currentOrder, currentPrice);
    }

    private boolean shouldExecuteOrder(Orders order, int currentPrice) {
        if (order.getTradeType() == TradeType.BUY) {
            return currentPrice <= order.getPrice();
        } else if (order.getTradeType() == TradeType.SELL) {
            return currentPrice >= order.getPrice();
        }
        return false;
    }


    /**
     * 사용자별 락을 사용한 주문 처리 - 세마포어보다 효율적
     */
    private void executeOrderWithLock(Orders order, int executionPrice) {
        Long memberId = order.getMembers().getId();
        ReentrantLock memberLock = getMemberLock(memberId);

        if (!memberLock.tryLock()) {
            log.debug("사용자 락 획득 실패로 주문 처리 스킵. orderId={}, memberId={}",
                    order.getId(), memberId);
            return;
        }

        try {
            StockPriceDto refreshedPrice = hantuWebSocketHandler.getLatestPrice(order.getStockCode());
            if (refreshedPrice == null) {
                log.warn("락 획득 후 가격 정보 조회 실패. orderId={}", order.getId());
                return;
            }

            int finalPrice = refreshedPrice.getCurrentPrice();
            if (!shouldExecuteOrder(order, finalPrice)) {
                log.debug("락 획득 후 체결 조건 불만족. orderId={}", order.getId());
                return;
            }
            executeOrder(order, finalPrice);
        } finally {
            memberLock.unlock();
        }
    }

    /**
     * 핵심 주문 실행 로직 - DB 락에만 의존하여 단순화
     */
    private void executeOrder(Orders order, int executionPrice) {
        if (order.getTradeType() == TradeType.SELL) {
            Optional<Portfolios> optionalPortfolio = portfoliosRepository
                    .findByMembersIdAndStockCodeWithLock(order.getMembers().getId(), order.getStockCode());

            if (optionalPortfolio.isEmpty()) {
                order.cancel();
                ordersRepository.save(order);
                log.warn("보유 주식 없음으로 주문 취소. orderId={}", order.getId());
                return;
            }

            Portfolios portfolio = optionalPortfolio.get();
            if (portfolio.getQuantity() < order.getQuantity()) {
                order.cancel();
                ordersRepository.save(order);
                log.warn("보유 수량 부족으로 주문 취소. orderId={}, 보유={}, 주문={}",
                        order.getId(), portfolio.getQuantity(), order.getQuantity());
                return;
            }
        }

        order.execute();
        ordersRepository.save(order);
        Members member = order.getMembers();

        Trades trade = new Trades(
                order.getStockCode(),
                order.getStockName(),
                order.getTradeType(),
                order.getQuantity(),
                executionPrice,
                member);
        tradesRepository.save(trade);

        updateMemberBalanceAndPortfolio(order, member, executionPrice);

        sendNotificationSafely(member, order, executionPrice);

        log.info("주문 체결 완료. orderId={}, memberId={}, type={}, price={}, quantity={}",
                order.getId(), member.getId(), order.getTradeType(), executionPrice, order.getQuantity());
    }

    private void updateMemberBalanceAndPortfolio(Orders order, Members member, int executionPrice) {
        if (order.getTradeType() == TradeType.BUY) {
            int frozenAmount = order.getPrice() * order.getQuantity();
            int actualAmount = executionPrice * order.getQuantity();
            int refundAmount = frozenAmount - actualAmount;

            member.setCashBalance(member.getCashBalance() + refundAmount);
            portfoliosService.updateForBuy(
                    member.getId(),
                    order.getStockCode(),
                    order.getStockName(),
                    order.getQuantity(),
                    executionPrice);

        } else if (order.getTradeType() == TradeType.SELL) {
            int actualAmount = executionPrice * order.getQuantity();
            member.setCashBalance(member.getCashBalance() + actualAmount);
            portfoliosService.updateForSell(
                    member.getId(),
                    order.getStockCode(),
                    order.getQuantity());
        }
    }

    private void sendNotificationSafely(Members member, Orders order, int executionPrice) {
        try {
            notificationsService.sendTradeNotification(
                    member.getId(),
                    order.getStockCode(),
                    order.getStockName(),
                    order.getTradeType(),
                    order.getQuantity(),
                    executionPrice);
        } catch (Exception e) {
            log.error("지정가 {} 알림 발송 실패 - 사용자: {}, 종목: {}",
                    order.getTradeType(), member.getId(), order.getStockCode(), e);
        }
    }
}