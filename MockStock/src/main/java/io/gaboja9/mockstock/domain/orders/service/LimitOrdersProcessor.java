package io.gaboja9.mockstock.domain.orders.service;

import io.gaboja9.mockstock.domain.orders.entity.OrderStatus;
import io.gaboja9.mockstock.domain.orders.entity.OrderType;
import io.gaboja9.mockstock.domain.orders.entity.Orders;
import io.gaboja9.mockstock.domain.orders.repository.OrdersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LimitOrdersProcessor {

    private final OrdersRepository ordersRepository;
    private final OrdersService ordersService;
    private final LimitOrdersExecutor limitOrdersExecutor;
    private final Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void processLimitOrders() {
        if (!ordersService.openKoreanMarket()) {
            return;
        }
        try {
            List<Orders> pendingOrders = ordersRepository
                    .findByStatusAndOrderTypeOrderByCreatedAtAsc(OrderStatus.PENDING, OrderType.LIMIT);
            if (pendingOrders.isEmpty()) {
                return;
            }
            processBatchOrders(pendingOrders);
        } catch (Exception e) {
            log.error("지정가 주문 처리 스케줄러 오류", e);
        }
    }

    private void processBatchOrders(List<Orders> pendingOrders) {
        log.debug("처리할 지정가 주문 수: {}", pendingOrders.size());

        List<CompletableFuture<Void>> futures = pendingOrders.stream()
                .map(this::processOrderAsync)
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(30, TimeUnit.SECONDS);
            log.debug("주문 처리 배치 완료. 처리된 주문: {}", pendingOrders.size());

        } catch (TimeoutException e) {
            log.warn("주문 처리 타임아웃 발생. 처리 중인 주문: {}", pendingOrders.size());

        } catch (ExecutionException e) {
            log.error("주문 처리 중 치명적 오류 발생", e.getCause());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("주문 처리 중 인터럽트 발생", e);
            futures.forEach(future -> future.cancel(true));
        }
    }

    private CompletableFuture<Void> processOrderAsync(Orders order) {
        return CompletableFuture.runAsync(() ->
                limitOrdersExecutor.processIndividualOrder(order), virtualThreadExecutor);
    }
}