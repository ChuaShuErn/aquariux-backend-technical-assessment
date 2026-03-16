package com.aquariux.technical.assessment.trade.controller;

import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.service.WalletServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@Tag(name = "Wallet", description = "User wallet operations")
@RequiredArgsConstructor
public class WalletController {

    private final WalletServiceInterface walletService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user wallet balances", description = "Retrieve all wallet balances for a specific user")
    public ResponseEntity<List<WalletBalanceResponse>> getUserWallets(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getUserWalletBalances(userId));
    }
}