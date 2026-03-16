package com.aquariux.technical.assessment.trade.service;

import java.util.List;

import com.aquariux.technical.assessment.trade.dto.request.TradeRequest;
import com.aquariux.technical.assessment.trade.dto.response.TradeResponse;

public interface TradeServiceInterface {
	TradeResponse executeTrade(TradeRequest tradeRequest);

	List<TradeResponse> getTradeHistory(Long userId);
}