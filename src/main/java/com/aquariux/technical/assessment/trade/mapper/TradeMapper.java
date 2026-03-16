package com.aquariux.technical.assessment.trade.mapper;

import com.aquariux.technical.assessment.trade.entity.Trade;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TradeMapper {

    @Insert("""
            INSERT INTO trades (user_id, crypto_pair_id, trade_type, quantity, price, total_amount)
            VALUES (#{userId}, #{cryptoPairId}, #{tradeType}, #{quantity}, #{price}, #{totalAmount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTrade(Trade trade);

    @Select("""
            SELECT t.id, t.user_id as userId, t.crypto_pair_id as cryptoPairId,
                   t.trade_type as tradeType, t.quantity, t.price,
                   t.total_amount as totalAmount, t.trade_time as tradeTime,
                   cp.pair_name as pairName
            FROM trades t
            INNER JOIN crypto_pairs cp ON t.crypto_pair_id = cp.id
            WHERE t.user_id = #{userId}
            ORDER BY t.trade_time DESC
            """)
    List<Trade> findByUserId(Long userId);
}