package com.aquariux.technical.assessment.trade.entity;

import lombok.Data;

@Data
public class Symbol {
    private Long id;
    private String symbol;
    private String name;
    private Boolean active;
}