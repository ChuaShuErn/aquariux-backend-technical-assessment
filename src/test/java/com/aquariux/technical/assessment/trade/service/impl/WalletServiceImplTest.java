package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.mapper.UserWalletMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private UserWalletMapper userWalletMapper;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UserWalletDto btcWallet;
    private UserWalletDto ethWallet;
    private UserWalletDto usdtWallet;

    @BeforeEach
    void setUp() {
        btcWallet = new UserWalletDto();
        btcWallet.setId(1L);
        btcWallet.setUserId(1L);
        btcWallet.setSymbolId(1L);
        btcWallet.setBalance(new BigDecimal("0.5"));
        btcWallet.setSymbol("BTC");
        btcWallet.setName("Bitcoin");
        btcWallet.setUpdatedAt(LocalDateTime.now());

        ethWallet = new UserWalletDto();
        ethWallet.setId(2L);
        ethWallet.setUserId(1L);
        ethWallet.setSymbolId(2L);
        ethWallet.setBalance(new BigDecimal("2.0"));
        ethWallet.setSymbol("ETH");
        ethWallet.setName("Ethereum");
        ethWallet.setUpdatedAt(LocalDateTime.now());

        usdtWallet = new UserWalletDto();
        usdtWallet.setId(3L);
        usdtWallet.setUserId(1L);
        usdtWallet.setSymbolId(3L);
        usdtWallet.setBalance(new BigDecimal("10000.00"));
        usdtWallet.setSymbol("USDT");
        usdtWallet.setName("Tether");
        usdtWallet.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getUserWalletBalances_ShouldReturnMappedWallets() {
        // Given
        Long userId = 1L;
        List<UserWalletDto> mockWallets = Arrays.asList(btcWallet, ethWallet, usdtWallet);
        when(userWalletMapper.findByUserId(userId)).thenReturn(mockWallets);

        // When
        List<WalletBalanceResponse> result = walletService.getUserWalletBalances(userId);

        // Then
        assertThat(result).hasSize(3);

        WalletBalanceResponse btcResponse = result.get(0);
        assertThat(btcResponse.getSymbol()).isEqualTo("BTC");
        assertThat(btcResponse.getName()).isEqualTo("Bitcoin");
        assertThat(btcResponse.getBalance()).isEqualTo(new BigDecimal("0.5"));

        WalletBalanceResponse ethResponse = result.get(1);
        assertThat(ethResponse.getSymbol()).isEqualTo("ETH");
        assertThat(ethResponse.getName()).isEqualTo("Ethereum");
        assertThat(ethResponse.getBalance()).isEqualTo(new BigDecimal("2.0"));

        WalletBalanceResponse usdtResponse = result.get(2);
        assertThat(usdtResponse.getSymbol()).isEqualTo("USDT");
        assertThat(usdtResponse.getName()).isEqualTo("Tether");
        assertThat(usdtResponse.getBalance()).isEqualTo(new BigDecimal("10000.00"));
    }

    @Test
    void getUserWalletBalances_WhenNoWallets_ShouldReturnEmptyList() {
        // Given
        Long userId = 999L;
        when(userWalletMapper.findByUserId(userId)).thenReturn(Arrays.asList());

        // When
        List<WalletBalanceResponse> result = walletService.getUserWalletBalances(userId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getUserWalletBalances_WithZeroBalance_ShouldReturnWalletWithZeroBalance() {
        // Given
        Long userId = 2L;
        UserWalletDto zeroBalanceWallet = new UserWalletDto();
        zeroBalanceWallet.setId(4L);
        zeroBalanceWallet.setUserId(userId);
        zeroBalanceWallet.setSymbolId(1L);
        zeroBalanceWallet.setBalance(BigDecimal.ZERO);
        zeroBalanceWallet.setSymbol("BTC");
        zeroBalanceWallet.setName("Bitcoin");
        
        when(userWalletMapper.findByUserId(userId)).thenReturn(Arrays.asList(zeroBalanceWallet));

        // When
        List<WalletBalanceResponse> result = walletService.getUserWalletBalances(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.get(0).getSymbol()).isEqualTo("BTC");
    }
}