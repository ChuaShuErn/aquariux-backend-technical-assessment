# Technical Assessment - Trading System Implementation

## Overview
Complete the implementation of a cryptocurrency trading system. The foundation has been provided - your task is to implement the core trading functionality.

## Assessment Task
Implement the **Trade Execution API** that allows users to buy and sell cryptocurrency pairs.

### Current State
- ✅ Database schema and sample data ready
- ✅ User wallet system implemented
- ✅ Price aggregation system implemented
- ✅ Basic project structure provided
- 🚧 **Trade execution system - TO BE IMPLEMENTED**

## Your Mission
Complete the `POST /api/trades/execute` endpoint to handle cryptocurrency trading.

### Starting Points
1. **[TradeController](src/main/java/com/aquariux/technical/assessment/trade/controller/TradeController.java)** - `/api/trades/execute` endpoint
2. **[TradeServiceImpl](src/main/java/com/aquariux/technical/assessment/trade/service/impl/TradeServiceImpl.java)** - Core business logic implementation
3. **[TradeRequest](src/main/java/com/aquariux/technical/assessment/trade/dto/request/TradeRequest.java)/[TradeResponse](src/main/java/com/aquariux/technical/assessment/trade/dto/response/TradeResponse.java)** - Design the data structures
4. **[TradeMapper](src/main/java/com/aquariux/technical/assessment/trade/mapper/TradeMapper.java)** - Database operations

### Available Resources
- **Sample Users** - 10 users with various wallet balances
- **Sample Prices** - Historical price data for BTCUSDT and ETHUSDT
- **Existing APIs** - Wallet balance and price endpoints for reference

## Technical Requirements

### Functional Requirements
Complete the `POST /api/trades/execute` endpoint to handle cryptocurrency trading. The implementation should reflect your understanding of what a trading API should accomplish.

### Technical Constraints
- Use the existing project structure and patterns
- Follow the established coding conventions
- Implement proper error handling
- Ensure data consistency

### Implementation Flexibility
- **Make assumptions** if business requirements are unclear - document them clearly
- **Add new classes** if needed for your implementation (DTOs, services, exceptions, etc.)
- **Modify existing classes** where necessary to support your solution
- **Extend the data model** if your design requires additional fields or structures
- **Create new documentation files** for assumptions or design decisions - do not modify existing .md files

## Tips
- Test the current `/api/trades/execute` endpoint in Swagger UI with `{"userId": 1, "tradeType": "BUY"}` to see your starting point
- Consider breaking down the problem into manageable components
- Consider to follow current patterns in the existing codebase
- Consider the full request-response lifecycle
- Think about separation of concerns
- Consider what makes an API reliable in production

## Evaluation Criteria

- Implementation
- Design
- Problem Solving
- Professionalism

Good luck! 🚀