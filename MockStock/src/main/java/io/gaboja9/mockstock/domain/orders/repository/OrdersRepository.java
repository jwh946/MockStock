package io.gaboja9.mockstock.domain.orders.repository;

import io.gaboja9.mockstock.domain.orders.entity.OrderStatus;
import io.gaboja9.mockstock.domain.orders.entity.OrderType;
import io.gaboja9.mockstock.domain.orders.entity.Orders;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Orders o JOIN FETCH o.members WHERE o.id = :id")
    Optional<Orders> findByIdWithMember(@Param("id") Long id);

    @Query(
            "SELECT o FROM Orders o JOIN FETCH o.members WHERE o.status = :status AND o.orderType ="
                    + " :orderType ORDER BY o.createdAt ASC")
    List<Orders> findByStatusAndOrderTypeOrderByCreatedAtAsc(
            @Param("status") OrderStatus status, @Param("orderType") OrderType orderType);

    void deleteByMembersId(Long memberId);
}
