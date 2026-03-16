package com.aquariux.technical.assessment.trade.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aquariux.technical.assessment.trade.dto.request.TradeRequest;
import com.aquariux.technical.assessment.trade.dto.response.TradeResponse;
import com.aquariux.technical.assessment.trade.service.TradeServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trades")
@Tag(name = "Trade", description = "Trading operations")
@RequiredArgsConstructor
public class TradeController {

	private final TradeServiceInterface tradeService;

	@PostMapping(value = "/execute", produces = "application/json")
	@Operation(summary = "Execute trade", description = "Execute a buy or sell trade for cryptocurrency pairs")
	public ResponseEntity<TradeResponse> executeTrade(@RequestBody TradeRequest tradeRequest) {
		return ResponseEntity.ok(tradeService.executeTrade(tradeRequest));
	}

	@GetMapping("/history/{userId}")
	@Operation(summary = "Get trade history", description = "Retrieve trade history for a specific user")
	public ResponseEntity<List<TradeResponse>> getTradeHistory(@PathVariable Long userId) {
		return ResponseEntity.ok(tradeService.getTradeHistory(userId));
	}

}