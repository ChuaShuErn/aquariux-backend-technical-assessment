package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.request.TradeRequest;
import com.aquariux.technical.assessment.trade.dto.response.TradeResponse;
import com.aquariux.technical.assessment.trade.entity.*;
import com.aquariux.technical.assessment.trade.enums.TradeType;
import com.aquariux.technical.assessment.trade.exception.InsufficientBalanceException;
import com.aquariux.technical.assessment.trade.exception.InvalidTradeRequestException;
import com.aquariux.technical.assessment.trade.exception.ResourceNotFoundException;
import com.aquariux.technical.assessment.trade.mapper.*;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceImplTest {

    @Mock
    private TradeMapper tradeMapper;
    @Mock
    private CryptoPairMapper cryptoPairMapper;
    @Mock
    private CryptoPriceMapper cryptoPriceMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserWalletMapper userWalletMapper;

    @InjectMocks
    private TradeServiceImpl tradeService;

    // Symbol IDs matching V1 schema seed data
    private static final Long BTC_SYMBOL_ID = 1L;
    private static final Long ETH_SYMBOL_ID = 2L;
    private static final Long USDT_SYMBOL_ID = 3L;

    private User testUser;
    private CryptoPair btcusdtPair;
    private CryptoPrice latestPrice;
    private UserWallet usdtWallet;
    private UserWallet btcWallet;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");
        testUser.setEmail("user1@example.com");

        btcusdtPair = new CryptoPair();
        btcusdtPair.setId(1L);
        btcusdtPair.setBaseSymbolId(BTC_SYMBOL_ID);
        btcusdtPair.setQuoteSymbolId(USDT_SYMBOL_ID);
        btcusdtPair.setPairName("BTCUSDT");
        btcusdtPair.setActive(true);

        latestPrice = new CryptoPrice();
        latestPrice.setId(1L);
        latestPrice.setCryptoPairId(1L);
        latestPrice.setBidPrice(new BigDecimal("46080.40"));
        latestPrice.setAskPrice(new BigDecimal("46070.40"));
        latestPrice.setCreatedAt(LocalDateTime.now());

        usdtWallet = new UserWallet();
        usdtWallet.setId(1L);
        usdtWallet.setUserId(1L);
        usdtWallet.setSymbolId(USDT_SYMBOL_ID);
        usdtWallet.setBalance(new BigDecimal("10000.00"));

        btcWallet = new UserWallet();
        btcWallet.setId(2L);
        btcWallet.setUserId(1L);
        btcWallet.setSymbolId(BTC_SYMBOL_ID);
        btcWallet.setBalance(new BigDecimal("0.50000000"));
    }

    @Test
    void executeTrade_BuySuccess_ShouldReturnTradeResponse() {
        // Given
        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("BTCUSDT");
        request.setQuantity(new BigDecimal("0.1"));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(cryptoPairMapper.findByPairName("BTCUSDT")).thenReturn(btcusdtPair);
        when(cryptoPriceMapper.findLatestByPairId(1L)).thenReturn(latestPrice);
        when(userWalletMapper.findByUserIdAndSymbolId(1L, USDT_SYMBOL_ID)).thenReturn(usdtWallet);  // debit USDT
        when(userWalletMapper.findByUserIdAndSymbolId(1L, BTC_SYMBOL_ID)).thenReturn(null);     // no BTC wallet yet
        when(userWalletMapper.updateBalance(any(UserWallet.class))).thenReturn(1); // optimistic lock passes

        // When
        TradeResponse response = tradeService.executeTrade(request);

        // Then
        assertThat(response.getTradeType()).isEqualTo("BUY");
        assertThat(response.getExecutionPrice()).isEqualByComparingTo(new BigDecimal("46070.40"));
        assertThat(response.getPairName()).isEqualTo("BTCUSDT");
        assertThat(response.getQuantity()).isEqualByComparingTo(new BigDecimal("0.1"));
        verify(tradeMapper).insertTrade(any(Trade.class));
        verify(userWalletMapper).insertWallet(any(UserWallet.class)); // new BTC wallet created
    }

    @Test
    void executeTrade_SellSuccess_ShouldUseBidPrice() {
        // Given
        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.SELL);
        request.setPairName("BTCUSDT");
        request.setQuantity(new BigDecimal("0.1"));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(cryptoPairMapper.findByPairName("BTCUSDT")).thenReturn(btcusdtPair);
        when(cryptoPriceMapper.findLatestByPairId(1L)).thenReturn(latestPrice);
        when(userWalletMapper.findByUserIdAndSymbolId(1L, BTC_SYMBOL_ID)).thenReturn(btcWallet);    // debit BTC
        when(userWalletMapper.findByUserIdAndSymbolId(1L, USDT_SYMBOL_ID)).thenReturn(usdtWallet);  // credit USDT
        when(userWalletMapper.updateBalance(any(UserWallet.class))).thenReturn(1); // optimistic lock passes

        // When
        TradeResponse response = tradeService.executeTrade(request);

        // Then
        assertThat(response.getTradeType()).isEqualTo("SELL");
        assertThat(response.getExecutionPrice()).isEqualByComparingTo(new BigDecimal("46080.40"));
        verify(tradeMapper).insertTrade(any(Trade.class));
    }

    @Test
    void executeTrade_InsufficientBalance_ShouldThrow() {
        // Given — only 100 USDT but trying to buy 1 BTC
        usdtWallet.setBalance(new BigDecimal("100.00"));
        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("BTCUSDT");
        request.setQuantity(new BigDecimal("1"));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(cryptoPairMapper.findByPairName("BTCUSDT")).thenReturn(btcusdtPair);
        when(cryptoPriceMapper.findLatestByPairId(1L)).thenReturn(latestPrice);
        when(userWalletMapper.findByUserIdAndSymbolId(1L, USDT_SYMBOL_ID)).thenReturn(usdtWallet);

        // When/Then
        assertThatThrownBy(() -> tradeService.executeTrade(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void executeTrade_UserNotFound_ShouldThrow() {
        // Given
        TradeRequest request = new TradeRequest();
        request.setUserId(999L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("BTCUSDT");
        request.setQuantity(new BigDecimal("0.1"));

        when(userMapper.findById(999L)).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> tradeService.executeTrade(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void executeTrade_InvalidPair_ShouldThrow() {
        // Given
        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("DOGEUSD");
        request.setQuantity(new BigDecimal("1"));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(cryptoPairMapper.findByPairName("DOGEUSD")).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> tradeService.executeTrade(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pair");
    }

    @Test
    void executeTrade_NullQuantity_ShouldThrow() {
        // Given
        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("BTCUSDT");
        request.setQuantity(null);

        // When/Then
        assertThatThrownBy(() -> tradeService.executeTrade(request))
                .isInstanceOf(InvalidTradeRequestException.class)
                .hasMessageContaining("Quantity");
    }

    @Test
    void executeTrade_CreatesWalletOnFirstHold() {
        // Given — user buys BTC for the first time (no BTC wallet)
        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("BTCUSDT");
        request.setQuantity(new BigDecimal("0.01"));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(cryptoPairMapper.findByPairName("BTCUSDT")).thenReturn(btcusdtPair);
        when(cryptoPriceMapper.findLatestByPairId(1L)).thenReturn(latestPrice);
        when(userWalletMapper.findByUserIdAndSymbolId(1L, USDT_SYMBOL_ID)).thenReturn(usdtWallet);
        when(userWalletMapper.findByUserIdAndSymbolId(1L, BTC_SYMBOL_ID)).thenReturn(null); // no BTC wallet
        when(userWalletMapper.updateBalance(any(UserWallet.class))).thenReturn(1); // optimistic lock passes

        // When
        tradeService.executeTrade(request);

        // Then — insertWallet called (not updateBalance) for BTC
        verify(userWalletMapper).insertWallet(any(UserWallet.class));
    }

    @Test
    void executeTrade_BuyEthSuccess_ShouldUseAskPrice() {
        // Given — ETHUSDT pair
        CryptoPair ethusdtPair = new CryptoPair();
        ethusdtPair.setId(2L);
        ethusdtPair.setBaseSymbolId(ETH_SYMBOL_ID);
        ethusdtPair.setQuoteSymbolId(USDT_SYMBOL_ID);
        ethusdtPair.setPairName("ETHUSDT");
        ethusdtPair.setActive(true);

        CryptoPrice ethPrice = new CryptoPrice();
        ethPrice.setId(2L);
        ethPrice.setCryptoPairId(2L);
        ethPrice.setBidPrice(new BigDecimal("3177.50"));
        ethPrice.setAskPrice(new BigDecimal("3172.50"));
        ethPrice.setCreatedAt(LocalDateTime.now());

        TradeRequest request = new TradeRequest();
        request.setUserId(1L);
        request.setTradeType(TradeType.BUY);
        request.setPairName("ETHUSDT");
        request.setQuantity(new BigDecimal("1.0"));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(cryptoPairMapper.findByPairName("ETHUSDT")).thenReturn(ethusdtPair);
        when(cryptoPriceMapper.findLatestByPairId(2L)).thenReturn(ethPrice);
        when(userWalletMapper.findByUserIdAndSymbolId(1L, USDT_SYMBOL_ID)).thenReturn(usdtWallet);  // debit USDT
        when(userWalletMapper.findByUserIdAndSymbolId(1L, ETH_SYMBOL_ID)).thenReturn(null);          // no ETH wallet
        when(userWalletMapper.updateBalance(any(UserWallet.class))).thenReturn(1); // optimistic lock passes

        // When
        TradeResponse response = tradeService.executeTrade(request);

        // Then
        assertThat(response.getTradeType()).isEqualTo("BUY");
        assertThat(response.getPairName()).isEqualTo("ETHUSDT");
        assertThat(response.getExecutionPrice()).isEqualByComparingTo(new BigDecimal("3172.50")); // ASK price
        verify(userWalletMapper).insertWallet(any(UserWallet.class)); // new ETH wallet created
    }

    @Test
    void getTradeHistory_ShouldReturnMappedTrades() {
        // Given
        Trade trade1 = new Trade();
        trade1.setId(1L);
        trade1.setUserId(1L);
        trade1.setPairName("BTCUSDT");
        trade1.setTradeType("BUY");
        trade1.setQuantity(new BigDecimal("0.1"));
        trade1.setPrice(new BigDecimal("46070.40"));
        trade1.setTotalAmount(new BigDecimal("4607.04"));
        trade1.setTradeTime(LocalDateTime.now());

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(tradeMapper.findByUserId(1L)).thenReturn(Arrays.asList(trade1));

        // When
        List<TradeResponse> result = tradeService.getTradeHistory(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPairName()).isEqualTo("BTCUSDT");
        assertThat(result.get(0).getTradeType()).isEqualTo("BUY");
        assertThat(result.get(0).getQuantity()).isEqualByComparingTo(new BigDecimal("0.1"));
    }
}
