package com.aquariux.technical.assessment.trade.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BestPriceResponse {
    private String pairName;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private String bidSource;
    private String askSource;
    private LocalDateTime timestamp;
}