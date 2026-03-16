package com.aquariux.technical.assessment.trade.controller;

import com.aquariux.technical.assessment.trade.dto.response.BestPriceResponse;
import com.aquariux.technical.assessment.trade.service.PriceServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@Tag(name = "Price", description = "Cryptocurrency price operations")
@RequiredArgsConstructor
public class PriceController {

    private final PriceServiceInterface priceService;

    @GetMapping("/latest")
    @Operation(summary = "Get latest best prices", description = "Retrieve the latest aggregated best prices for all crypto pairs")
    public ResponseEntity<List<BestPriceResponse>> getLatestBestPrices() {
        return ResponseEntity.ok(priceService.getLatestBestPrices());
    }
}