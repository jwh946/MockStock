package io.gaboja9.mockstock.domain.orders.service;

import io.gaboja9.mockstock.domain.members.entity.Members;
import io.gaboja9.mockstock.domain.members.exception.NotFoundMemberException;
import io.gaboja9.mockstock.domain.members.repository.MembersRepository;
import io.gaboja9.mockstock.domain.notifications.service.NotificationsService;
import io.gaboja9.mockstock.domain.orders.dto.request.OrdersLimitTypeRequestDto;
import io.gaboja9.mockstock.domain.orders.dto.request.OrdersMarketTypeRequestDto;
import io.gaboja9.mockstock.domain.orders.dto.response.OrderResponseDto;
import io.gaboja9.mockstock.domain.orders.entity.OrderType;
import io.gaboja9.mockstock.domain.orders.entity.Orders;
import io.gaboja9.mockstock.domain.orders.exception.*;
import io.gaboja9.mockstock.domain.orders.repository.OrdersRepository;
import io.gaboja9.mockstock.domain.portfolios.entity.Portfolios;
import io.gaboja9.mockstock.domain.portfolios.exception.NotFoundPortfolioException;
import io.gaboja9.mockstock.domain.portfolios.repository.PortfoliosRepository;
import io.gaboja9.mockstock.domain.portfolios.service.PortfoliosService;
import io.gaboja9.mockstock.domain.trades.entity.TradeType;
import io.gaboja9.mockstock.domain.trades.entity.Trades;
import io.gaboja9.mockstock.domain.trades.repository.TradesRepository;
import io.gaboja9.mockstock.global.websocket.HantuWebSocketHandler;
import io.gaboja9.mockstock.global.websocket.dto.StockPriceDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final MembersRepository membersRepository;
    private final OrdersRepository ordersRepository;
    private final TradesRepository tradesRepository;
    private final PortfoliosService portfoliosService;
    private final PortfoliosRepository portfoliosRepository;
    private final HantuWebSocketHandler hantuWebSocketHandler;
    private final NotificationsService notificationsService;

    @FunctionalInterface
    private interface OrderTask<T> {
        T execute();
    }

    private <T> T executeOrderSafely(OrderTask<T> task) {
        if (!openKoreanMarket()) {
            throw new NotOpenKoreanMarketException();
        }

        try {
            return task.execute();
        } catch (Exception e) {
            log.error("주문 처리 중 오류 발생", e);
            throw e;
        }
    }

    @Transactional
    public OrderResponseDto executeMarketBuyOrders(Long memberId, OrdersMarketTypeRequestDto dto) {
        return executeOrderSafely(() -> {
            Members findMember = membersRepository
                    .findByIdWithLock(memberId)
                    .orElseThrow(() -> new NotFoundMemberException(memberId));

            String stockCode = dto.getStockCode();
            String stockName = dto.getStockName();
            int quantity = dto.getQuantity();

            Integer currentPrice = getCurrentPriceOrNull(stockCode);
            if (currentPrice == null) {
                return OrderResponseDto.builder()
                        .executed(false)
                        .message("시장가 매수 실패: 현재 가격 정보를 불러올 수 없습니다.")
                        .build();
            }

            int totalPrice = currentPrice * quantity;
            int cashBalance = findMember.getCashBalance();

            if (cashBalance < totalPrice) {
                throw new NotEnoughCashException(cashBalance);
            }

            Orders order = new Orders(
                    stockCode, stockName, OrderType.MARKET, TradeType.BUY,
                    quantity, currentPrice, findMember);
            order.execute();
            ordersRepository.save(order);

            Trades trade = new Trades(
                    stockCode, stockName, TradeType.BUY,
                    quantity, currentPrice, findMember);
            tradesRepository.save(trade);

            findMember.setCashBalance(findMember.getCashBalance() - totalPrice);
            portfoliosService.updateForBuy(memberId, stockCode, stockName, quantity, currentPrice);

            sendTradeNotificationSafely(memberId, stockCode, stockName, TradeType.BUY, quantity, currentPrice);

            log.info("시장가 매수 완료. memberId={}, stockCode={}, quantity={}, price={}",
                    memberId, stockCode, quantity, currentPrice);

            return OrderResponseDto.builder()
                    .executed(true)
                    .message("시장가 매수 완료")
                    .price(currentPrice)
                    .build();
        });
    }

    @Transactional
    public OrderResponseDto executeMarketSellOrders(Long memberId, OrdersMarketTypeRequestDto dto) {
        return executeOrderSafely(() -> {
            Members findMember = membersRepository
                    .findByIdWithLock(memberId)
                    .orElseThrow(() -> new NotFoundMemberException(memberId));

            String stockCode = dto.getStockCode();
            String stockName = dto.getStockName();
            int quantity = dto.getQuantity();

            Portfolios portfolio = portfoliosRepository
                    .findByMembersIdAndStockCodeWithLock(memberId, stockCode)
                    .orElseThrow(NotFoundPortfolioException::new);

            if (portfolio.getQuantity() < quantity) {
                throw new InvalidSellQuantityException(quantity);
            }

            Integer currentPrice = getCurrentPriceOrNull(stockCode);
            if (currentPrice == null) {
                return OrderResponseDto.builder()
                        .executed(false)
                        .message("시장가 매도 실패: 현재 가격 정보를 불러올 수 없습니다.")
                        .build();
            }

            int totalAmount = currentPrice * quantity;

            Orders order = new Orders(
                    stockCode, stockName, OrderType.MARKET, TradeType.SELL,
                    quantity, currentPrice, findMember);
            order.execute();
            ordersRepository.save(order);

            Trades trades = new Trades(
                    stockCode, stockName, TradeType.SELL,
                    quantity, currentPrice, findMember);
            tradesRepository.save(trades);

            portfoliosService.updateForSell(memberId, stockCode, quantity);
            findMember.setCashBalance(findMember.getCashBalance() + totalAmount);

            sendTradeNotificationSafely(memberId, stockCode, stockName, TradeType.SELL, quantity, currentPrice);

            log.info("시장가 매도 완료. memberId={}, stockCode={}, quantity={}, price={}",
                    memberId, stockCode, quantity, currentPrice);

            return OrderResponseDto.builder()
                    .executed(true)
                    .message("시장가 매도 완료")
                    .price(currentPrice)
                    .build();
        });
    }

    @Transactional
    public OrderResponseDto executeLimitBuyOrders(Long memberId, OrdersLimitTypeRequestDto dto) {
        return executeOrderSafely(() -> {
            Members findMember = membersRepository
                    .findByIdWithLock(memberId)
                    .orElseThrow(() -> new NotFoundMemberException(memberId));

            String stockCode = dto.getStockCode();
            String stockName = dto.getStockName();
            int limitPrice = dto.getPrice();
            int quantity = dto.getQuantity();
            int totalAmount = limitPrice * quantity;

            if (findMember.getCashBalance() < totalAmount) {
                throw new NotEnoughCashException(findMember.getCashBalance());
            }

            Integer currentPrice = getCurrentPriceOrNull(stockCode);
            if (currentPrice == null) {
                return OrderResponseDto.builder()
                        .executed(false)
                        .message("지정가 매수 실패: 현재 가격 정보를 불러올 수 없습니다.")
                        .build();
            }

            Orders order = new Orders(
                    stockCode, stockName, OrderType.LIMIT, TradeType.BUY,
                    quantity, limitPrice, findMember);

            if (currentPrice <= limitPrice) {
                order.execute();
                ordersRepository.save(order);

                Trades trade = new Trades(
                        stockCode, stockName, TradeType.BUY,
                        quantity, currentPrice, findMember);
                tradesRepository.save(trade);

                int actualAmount = currentPrice * quantity;
                findMember.setCashBalance(findMember.getCashBalance() - actualAmount);
                portfoliosService.updateForBuy(memberId, stockCode, stockName, quantity, currentPrice);

                sendTradeNotificationSafely(memberId, stockCode, stockName, TradeType.BUY, quantity, currentPrice);

                log.info("지정가 매수 즉시 체결. memberId={}, stockCode={}, limitPrice={}, executedPrice={}, quantity={}",
                        memberId, stockCode, limitPrice, currentPrice, quantity);

                return OrderResponseDto.builder()
                        .executed(true)
                        .message("지정가 매수 즉시 체결 완료")
                        .price(currentPrice)
                        .build();
            } else {
                ordersRepository.save(order);
                findMember.setCashBalance(findMember.getCashBalance() - totalAmount);

                log.info("지정가 매수 주문 대기. memberId={}, stockCode={}, limitPrice={}, currentPrice={}, quantity={}",
                        memberId, stockCode, limitPrice, currentPrice, quantity);

                return OrderResponseDto.builder()
                        .executed(false)
                        .message("지정가 매수 주문 대기중")
                        .price(limitPrice)
                        .build();
            }
        });
    }

    @Transactional
    public OrderResponseDto executeLimitSellOrders(Long memberId, OrdersLimitTypeRequestDto dto) {
        return executeOrderSafely(() -> {
            Members findMember = membersRepository
                    .findByIdWithLock(memberId)
                    .orElseThrow(() -> new NotFoundMemberException(memberId));

            String stockCode = dto.getStockCode();
            String stockName = dto.getStockName();
            int limitPrice = dto.getPrice();
            int quantity = dto.getQuantity();

            Portfolios portfolio = portfoliosRepository
                    .findByMembersIdAndStockCodeWithLock(memberId, stockCode)
                    .orElseThrow(NotFoundPortfolioException::new);

            if (portfolio.getQuantity() < quantity) {
                throw new InvalidSellQuantityException(quantity);
            }

            Integer currentPrice = getCurrentPriceOrNull(stockCode);
            if (currentPrice == null) {
                return OrderResponseDto.builder()
                        .executed(false)
                        .message("지정가 매도 실패: 현재 가격 정보를 불러올 수 없습니다.")
                        .build();
            }

            Orders order = new Orders(
                    stockCode, stockName, OrderType.LIMIT, TradeType.SELL,
                    quantity, limitPrice, findMember);

            if (currentPrice >= limitPrice) {
                order.execute();
                ordersRepository.save(order);

                Trades trades = new Trades(
                        stockCode, stockName, TradeType.SELL,
                        quantity, currentPrice, findMember);
                tradesRepository.save(trades);

                int actualAmount = currentPrice * quantity;
                findMember.setCashBalance(findMember.getCashBalance() + actualAmount);
                portfoliosService.updateForSell(memberId, stockCode, quantity);

                sendTradeNotificationSafely(memberId, stockCode, stockName, TradeType.SELL, quantity, currentPrice);

                log.info("지정가 매도 즉시 체결. memberId={}, stockCode={}, limitPrice={}, executedPrice={}, quantity={}",
                        memberId, stockCode, limitPrice, currentPrice, quantity);

                return OrderResponseDto.builder()
                        .executed(true)
                        .message("지정가 매도 즉시 체결 완료")
                        .price(currentPrice)
                        .build();
            } else {
                ordersRepository.save(order);

                log.info("지정가 매도 주문 대기. memberId={}, stockCode={}, limitPrice={}, currentPrice={}, quantity={}",
                        memberId, stockCode, limitPrice, currentPrice, quantity);

                return OrderResponseDto.builder()
                        .executed(false)
                        .message("지정가 매도 주문 대기중")
                        .price(limitPrice)
                        .build();
            }
        });
    }

    private void sendTradeNotificationSafely(Long memberId, String stockCode, String stockName,
                                             TradeType tradeType, int quantity, int price) {
        try {
            notificationsService.sendTradeNotification(memberId, stockCode, stockName, tradeType, quantity, price);
        } catch (Exception e) {
            log.error("{} 알림 발송 실패 - 사용자: {}, 종목: {}", tradeType, memberId, stockName, e);
        }
    }

    private Integer getCurrentPriceOrNull(String stockCode) {
        StockPriceDto stockPrice = hantuWebSocketHandler.getLatestPrice(stockCode);
        if (stockPrice == null) {
            log.warn("현재 가격 정보를 불러올 수 없습니다: {}", stockCode);
            return null;
        }
        return stockPrice.getCurrentPrice();
    }

    @Transactional
    public void remove(Long memberId) {
        Members findMember = membersRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(memberId));
        ordersRepository.deleteByMembersId(findMember.getId());
    }

    public boolean openKoreanMarket() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        LocalTime marketOpen = LocalTime.of(9, 0);
        LocalTime marketClose = LocalTime.of(15, 30);

        return !currentTime.isBefore(marketOpen) && !currentTime.isAfter(marketClose);
    }

}