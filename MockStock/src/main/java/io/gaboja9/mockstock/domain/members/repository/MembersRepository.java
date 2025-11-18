package io.gaboja9.mockstock.domain.members.repository;

import io.gaboja9.mockstock.domain.members.entity.Members;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Members m WHERE m.id = :id")
    Optional<Members> findByIdWithLock(@Param("id") Long id);

    int countByYesterdayProfitRateGreaterThan(double rate);
    int countByBankruptcyCntGreaterThan(int bankruptcyCnt);
}
