package io.gaboja9.mockstock.domain.portfolios.entity;

import io.gaboja9.mockstock.domain.members.entity.Members;
import io.gaboja9.mockstock.domain.orders.exception.InvalidSellQuantityException;
import io.gaboja9.mockstock.global.common.BaseEntity;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "portfolios",
        indexes = {
                @Index(name = "idx_portfolio_member_stock",
                columnList = "members_id, stock_code")
})
public class Portfolios extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockName;

    private String stockCode;

    private int quantity;

    private int avgPrice;

    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id")
    private Members members;

    public Portfolios(
            String stockCode, String stockName, int quantity, int avgPrice, Members members) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.members = members;
    }

    public void updateForBuy(int additionalQuantity, int buyPrice) {
        int currentInvestment = this.quantity * this.avgPrice;

        int additionalInvestment = additionalQuantity * buyPrice;

        int newQuantity = this.quantity + additionalQuantity;

        int newAvgPrice = (currentInvestment + additionalInvestment) / newQuantity;

        this.quantity = newQuantity;
        this.avgPrice = newAvgPrice;
    }

    public void updateForSell(int sellQuantity) {

        if (sellQuantity > this.quantity) {
            throw new InvalidSellQuantityException(this.quantity);
        }

        this.quantity -= sellQuantity;
    }
}
