# Business Requirements - Aquariux Trading System

## Overview

Aquariux is building a cryptocurrency trading platform that allows users to buy and sell digital assets. The system needs to provide real-time pricing, secure trading capabilities, and comprehensive wallet management.

## Business Context

### Target Users
- Individual cryptocurrency traders
- Retail investors looking to diversify portfolios
- Users seeking a simple, secure trading experience

### Market Position
- Focus on popular cryptocurrency pairs (BTC, ETH, USDT)
- Competitive pricing through multiple exchange integrations
- User-friendly API for potential mobile/web applications

## Core Business Requirements

### 1. Trading Operations
**Requirement**: Users must be able to execute buy and sell orders for supported cryptocurrency pairs.

**Business Rules**:
- Users can only trade with available wallet balance
- All trades must be executed at current market prices
- Trade history must be maintained for audit purposes

**Current Supported Trading Pairs**:
- BTCUSDT (Bitcoin/Tether)
- ETHUSDT (Ethereum/Tether)

**Out of Scope**:
- Transaction fees implementation
- Advanced order types (limit, stop-loss)
- Trade cancellation functionality

### 2. Wallet Management
**Requirement**: Users need secure wallet functionality to manage their cryptocurrency holdings.

**Business Rules**:
- Wallet records are created only when users first acquire a cryptocurrency
- Wallet balances must be updated in real-time after trades
- Users can view current balances for all supported cryptocurrencies (BTC, ETH, USDT)

**Out of Scope**:
- Initial wallet funding (handled outside of application scope)
- Wallet-to-wallet transfers
- Withdrawal functionality

### 3. Price Discovery
**Requirement**: System must provide accurate, up-to-date cryptocurrency prices from multiple sources.

**Business Rules**:
- Prices aggregated from Binance and Huobi exchanges via scheduled tasks
- Best price selection (lowest ask, highest bid) across sources
- Price updates fetched periodically through scheduler integration with 3rd party APIs
- Historical price data maintained for analysis

**Current Supported Price Sources**:
- Binance API
- Huobi API

**Out of Scope**:
- Real-time price streaming
- Price alerts and notifications
- Custom price aggregation algorithms

### 4. User Management
**Requirement**: Basic user identification and account management.

**Business Rules**:
- Users identified by unique user ID
- User profiles contain basic information (username, email)

**Out of Scope**:
- Account creation and authentication
- User session management
- User profile management and updates

## Technical Business Requirements

### Performance
- API response times under 200ms for trading operations
- Support for concurrent users (minimum 100 simultaneous trades)
- 99.9% uptime during trading hours

### Security
- All trading operations must be logged (maintained in trade table)
- API endpoints protected against unauthorized access

### Assumptions
- All trade requests require userId parameter and are assumed to be pre-authenticated and secured
- User data handling compliant with data protection regulations

### Compliance
- Transaction records must be immutable once created
- All trades timestamped with precise execution time

## Success Metrics

### Trading Volume
- Target: Process minimum 1000 trades per day
- Average trade size: $100-$10,000 USD equivalent

### User Experience
- API response success rate > 99.5%
- Zero balance calculation errors
- Accurate price feeds with < 1 second latency

### System Reliability
- Zero data loss incidents
- Automated recovery from external API failures
- Complete transaction audit trail maintained

