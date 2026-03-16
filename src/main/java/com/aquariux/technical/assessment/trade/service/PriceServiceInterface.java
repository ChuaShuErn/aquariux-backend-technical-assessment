package com.aquariux.technical.assessment.trade.service;

import com.aquariux.technical.assessment.trade.dto.response.BestPriceResponse;

import java.util.List;

public interface PriceServiceInterface {
    List<BestPriceResponse> getLatestBestPrices();
}