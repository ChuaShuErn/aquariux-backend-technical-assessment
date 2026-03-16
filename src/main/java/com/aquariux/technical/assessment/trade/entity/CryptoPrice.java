package com.aquariux.technical.assessment.trade.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CryptoPrice {
    private Long id;
    private Long cryptoPairId;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private String bidSource;
    private String askSource;
    private LocalDateTime createdAt;
    private String pairName;
}