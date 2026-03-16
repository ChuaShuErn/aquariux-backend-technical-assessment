package com.aquariux.technical.assessment.trade.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Trade {
    private Long id;
    private Long userId;
    private Long cryptoPairId;
    private String tradeType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDateTime tradeTime;
}