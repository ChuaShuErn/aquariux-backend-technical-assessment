package com.aquariux.technical.assessment.trade.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletBalanceResponse {
    private String symbol;
    private String name;
    private BigDecimal balance;
}