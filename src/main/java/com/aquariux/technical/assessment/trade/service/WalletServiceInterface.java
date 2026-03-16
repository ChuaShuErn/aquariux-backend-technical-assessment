package com.aquariux.technical.assessment.trade.service;

import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;

import java.util.List;

public interface WalletServiceInterface {
    List<WalletBalanceResponse> getUserWalletBalances(Long userId);
}