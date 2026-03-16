package com.aquariux.technical.assessment.trade.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aquariux.technical.assessment.trade.dto.request.TradeRequest;
import com.aquariux.technical.assessment.trade.dto.response.TradeResponse;
import com.aquariux.technical.assessment.trade.entity.CryptoPair;
import com.aquariux.technical.assessment.trade.entity.CryptoPrice;
import com.aquariux.technical.assessment.trade.entity.Trade;
import com.aquariux.technical.assessment.trade.entity.User;
import com.aquariux.technical.assessment.trade.entity.UserWallet;
import com.aquariux.technical.assessment.trade.enums.TradeType;
import com.aquariux.technical.assessment.trade.exception.InsufficientBalanceException;
import com.aquariux.technical.assessment.trade.exception.InvalidTradeRequestException;
import com.aquariux.technical.assessment.trade.exception.ResourceNotFoundException;
import com.aquariux.technical.assessment.trade.mapper.CryptoPairMapper;
import com.aquariux.technical.assessment.trade.mapper.CryptoPriceMapper;
import com.aquariux.technical.assessment.trade.mapper.TradeMapper;
import com.aquariux.technical.assessment.trade.mapper.UserMapper;
import com.aquariux.technical.assessment.trade.mapper.UserWalletMapper;
import com.aquariux.technical.assessment.trade.service.TradeServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeServiceInterface {

	private final TradeMapper tradeMapper;

	private final CryptoPairMapper cryptoPairMapper;
	private final CryptoPriceMapper cryptoPriceMapper;
	private final UserMapper userMapper;
	private final UserWalletMapper userWalletMapper;

	@Override
	public List<TradeResponse> getTradeHistory(Long userId) {

		User user = userMapper.findById(userId);

		if (user == null) {
			throw new ResourceNotFoundException("User is not found");
		}

		List<Trade> tradeList = tradeMapper.findByUserId(userId);

		return tradeList.stream().map(this::mapToResponse).toList();

	}

	@Override
	@Transactional
	public TradeResponse executeTrade(TradeRequest tradeRequest) {

		validateTradeRequest(tradeRequest);

		User user = userMapper.findById(tradeRequest.getUserId());

		if (user == null) {
			throw new ResourceNotFoundException("User is not found");
		}

		CryptoPair pair = cryptoPairMapper.findByPairName(tradeRequest.getPairName());
		if (pair == null) {
			throw new ResourceNotFoundException("Pair is not found");
		}

		CryptoPrice price = cryptoPriceMapper.findLatestByPairId(pair.getId());
		if (price == null) {
			throw new ResourceNotFoundException("Price is not found");
		}

		Trade trade = processTradeRequest(pair, tradeRequest, price);

		return mapToResponse(trade);

	}

	private Trade processTradeRequest(CryptoPair pair, TradeRequest tradeRequest, CryptoPrice price) {

		BigDecimal executionPrice;
		Long debitSymbolId, creditSymbolId;
		BigDecimal debitAmount, creditAmount;
		BigDecimal quantity = tradeRequest.getQuantity();

		if (tradeRequest.getTradeType() == TradeType.BUY) {
			executionPrice = price.getAskPrice();
			debitSymbolId = pair.getQuoteSymbolId(); // pay USDT
			creditSymbolId = pair.getBaseSymbolId(); // receive BTC/ETH
		} else {
			executionPrice = price.getBidPrice();
			debitSymbolId = pair.getBaseSymbolId(); // pay BTC/ETH
			creditSymbolId = pair.getQuoteSymbolId(); // receive USDT
		}

		BigDecimal totalAmount;
		// Round against User
		if (tradeRequest.getTradeType() == TradeType.BUY) {
			// User pays — round up (platform favorable)
			totalAmount = quantity.multiply(executionPrice).setScale(8, RoundingMode.CEILING);
		} else {
			// User receives — round down (platform favorable)
			totalAmount = quantity.multiply(executionPrice).setScale(8, RoundingMode.FLOOR);
		}

		if (tradeRequest.getTradeType() == TradeType.BUY) {
			debitAmount = totalAmount; // USDT cost
			creditAmount = quantity; // crypto received
		} else {
			debitAmount = quantity; // crypto sold
			creditAmount = totalAmount; // USDT received
		}

		UserWallet debitWallet = userWalletMapper.findByUserIdAndSymbolId(tradeRequest.getUserId(), debitSymbolId);
		if (debitWallet == null) {
			throw new ResourceNotFoundException("Wallet not found for debit");
		}
		if (debitWallet.getBalance().compareTo(debitAmount) < 0) {
			throw new InsufficientBalanceException(
					"Insufficient balance. Required: " + debitAmount + ", Available: " + debitWallet.getBalance());
		}
		debitWallet.setBalance(debitWallet.getBalance().subtract(debitAmount));
		int debitUpdated = userWalletMapper.updateBalance(debitWallet);
		if (debitUpdated == 0) {
			throw new ResourceNotFoundException("Concurrent wallet modification detected. Please retry your trade.");
		}

		// Credit wallet — create if first hold
		UserWallet creditWallet = userWalletMapper.findByUserIdAndSymbolId(tradeRequest.getUserId(), creditSymbolId);
		if (creditWallet == null) {
			creditWallet = new UserWallet();
			creditWallet.setUserId(tradeRequest.getUserId());
			creditWallet.setSymbolId(creditSymbolId);
			creditWallet.setBalance(creditAmount);
			userWalletMapper.insertWallet(creditWallet);
		} else {
			creditWallet.setBalance(creditWallet.getBalance().add(creditAmount));
			int creditUpdated = userWalletMapper.updateBalance(creditWallet);
			if (creditUpdated == 0) {
				throw new ResourceNotFoundException("Concurrent wallet modification detected. Please retry your trade.");
			}
		}

		// Insert trade record
		Trade trade = new Trade();
		trade.setUserId(tradeRequest.getUserId());
		trade.setCryptoPairId(pair.getId());
		trade.setTradeType(tradeRequest.getTradeType().name());
		trade.setQuantity(quantity);
		trade.setPrice(executionPrice);
		trade.setTotalAmount(totalAmount);
		trade.setTradeTime(LocalDateTime.now());
		trade.setPairName(tradeRequest.getPairName());

		tradeMapper.insertTrade(trade);

		log.info("Trade executed: userId={}, {} {} qty={} @ {}, total={}", tradeRequest.getUserId(),
				tradeRequest.getTradeType(), tradeRequest.getPairName(), quantity, executionPrice, totalAmount);

		return trade;

	}

	private void validateTradeRequest(TradeRequest tradeRequest) {

		// 1. unsupported user
		if (tradeRequest.getUserId() == null) {
			throw new InvalidTradeRequestException("UserId is null");
		}

		// 2. unsupported trade type
		if (tradeRequest.getTradeType() == null) {
			throw new InvalidTradeRequestException("Trade type is required (BUY or SELL)");
		}

		// 3. unsupported pair
		if (tradeRequest.getPairName() == null || tradeRequest.getPairName().isBlank()) {
			throw new InvalidTradeRequestException("Pair name is required (e.g., BTCUSDT)");
		}

		// 4. quantity cannot be negative or 0
		if (tradeRequest.getQuantity() == null || tradeRequest.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTradeRequestException("Quantity must be greater than zero");
		}

	}

	private TradeResponse mapToResponse(Trade trade) {
		TradeResponse response = new TradeResponse();
		response.setTradeId(trade.getId());
		response.setUserId(trade.getUserId());
		response.setPairName(trade.getPairName());
		response.setTradeType(trade.getTradeType());
		response.setQuantity(trade.getQuantity());
		response.setExecutionPrice(trade.getPrice());
		response.setTotalAmount(trade.getTotalAmount());
		response.setTradeTime(trade.getTradeTime());
		return response;
	}
}