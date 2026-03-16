package com.aquariux.technical.assessment.trade.dto.internal;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserWalletDto {
    private Long id;
    private Long userId;
    private Long symbolId;
    private BigDecimal balance;
    private LocalDateTime updatedAt;
    private String symbol;
    private String name;
}