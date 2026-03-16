# Database Schema

## Entity Relationship Diagram

```mermaid
erDiagram
    users {
        BIGINT id PK
        VARCHAR username UK
        VARCHAR email UK
        VARCHAR password
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    symbols {
        BIGINT id PK
        VARCHAR symbol UK "BTC, ETH, USDT"
        VARCHAR name "Bitcoin, Ethereum, Tether"
        BOOLEAN active
    }
    
    crypto_pairs {
        BIGINT id PK
        BIGINT base_symbol_id FK
        BIGINT quote_symbol_id FK
        VARCHAR pair_name UK "BTCUSDT, ETHUSDT"
        BOOLEAN active
    }
    
    crypto_prices {
        BIGINT id PK
        BIGINT crypto_pair_id FK
        DECIMAL bid_price
        DECIMAL ask_price
        VARCHAR bid_source "BINANCE/HUOBI"
        VARCHAR ask_source "BINANCE/HUOBI"
        TIMESTAMP created_at
    }
    
    trades {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT crypto_pair_id FK
        VARCHAR trade_type "BUY/SELL"
        DECIMAL quantity
        DECIMAL price
        DECIMAL total_amount
        TIMESTAMP trade_time
    }
    
    user_wallets {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT symbol_id FK
        DECIMAL balance
        TIMESTAMP updated_at
    }

    users ||--o{ trades : "places"
    users ||--o{ user_wallets : "owns"
    symbols ||--o{ crypto_pairs : "base_currency"
    symbols ||--o{ crypto_pairs : "quote_currency"
    symbols ||--o{ user_wallets : "holds"
    crypto_pairs ||--o{ trades : "traded_in"
    crypto_pairs ||--o{ crypto_prices : "has_prices"
```

### Viewing the Diagram
If the diagram above doesn't render in your editor, you can view it using online Mermaid viewers:

1. Go to https://mermaid.live/
2. Copy the entire code block above (from `erDiagram` to the last relationship line)
3. Paste it into the editor to see the visual diagram

Alternatively, explore the database structure directly using the H2 Console at http://localhost:8080/h2-console once your application is running.

## Table Descriptions

### Core Tables
- **[users](src/main/java/com/aquariux/technical/assessment/trade/entity/User.java)**: User accounts and authentication
  - `username`: VARCHAR(50) UNIQUE
  - `email`: VARCHAR(100) UNIQUE
  - `password`: VARCHAR(255)
- **[symbols](src/main/java/com/aquariux/technical/assessment/trade/entity/Symbol.java)**: Cryptocurrency symbols (BTC, ETH, USDT)
  - `symbol`: VARCHAR(10) UNIQUE
  - `name`: VARCHAR(50)
- **[crypto_pairs](src/main/java/com/aquariux/technical/assessment/trade/entity/CryptoPair.java)**: Trading pairs (BTCUSDT, ETHUSDT)
  - `pair_name`: VARCHAR(20) UNIQUE

### Trading Tables
- **[trades](src/main/java/com/aquariux/technical/assessment/trade/entity/Trade.java)**: Transaction records of buy/sell orders
  - `trade_type`: VARCHAR(4) CHECK ('BUY', 'SELL')
  - `quantity`, `price`, `total_amount`: DECIMAL(20,8)
- **[crypto_prices](src/main/java/com/aquariux/technical/assessment/trade/entity/CryptoPrice.java)**: Real-time price data from exchanges
  - `bid_price`, `ask_price`: DECIMAL(20,8)
  - `bid_source`, `ask_source`: VARCHAR(20)
- **[user_wallets](src/main/java/com/aquariux/technical/assessment/trade/entity/UserWallet.java)**: User cryptocurrency balances
  - `balance`: DECIMAL(20,8)
  - UNIQUE constraint on (user_id, symbol_id)