package com.aquariux.technical.assessment.trade.exception;

public class InvalidTradeRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InvalidTradeRequestException(String message) {
        super(message);
    }
}
