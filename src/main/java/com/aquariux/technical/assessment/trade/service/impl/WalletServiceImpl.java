package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.mapper.UserWalletMapper;
import com.aquariux.technical.assessment.trade.service.WalletServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletServiceInterface {

    private final UserWalletMapper userWalletMapper;

    public List<WalletBalanceResponse> getUserWalletBalances(Long userId) {
        List<UserWalletDto> wallets = userWalletMapper.findByUserId(userId);
        
        return wallets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private WalletBalanceResponse mapToResponse(UserWalletDto wallet) {
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setSymbol(wallet.getSymbol());
        response.setName(wallet.getName());
        response.setBalance(wallet.getBalance());
        return response;
    }
}