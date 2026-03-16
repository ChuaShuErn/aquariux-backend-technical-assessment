package com.aquariux.technical.assessment.trade.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserWallet {
    private Long id;
    private Long userId;
    private Long symbolId;
    private BigDecimal balance;
    private LocalDateTime updatedAt;
}