package com.aquariux.technical.assessment.trade.dto.request;

import com.aquariux.technical.assessment.trade.enums.TradeType;
import lombok.Data;

@Data
public class TradeRequest {
    private Long userId;
    private TradeType tradeType;
    
    // TODO: What information do you need to execute a trade?
}