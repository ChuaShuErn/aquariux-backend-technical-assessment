-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Symbols table (cryptocurrencies)
CREATE TABLE symbols (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

-- Crypto pairs table (all tradable pairs)
CREATE TABLE crypto_pairs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    base_symbol_id BIGINT NOT NULL,
    quote_symbol_id BIGINT NOT NULL,
    pair_name VARCHAR(20) NOT NULL UNIQUE,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (base_symbol_id) REFERENCES symbols(id),
    FOREIGN KEY (quote_symbol_id) REFERENCES symbols(id)
);

-- Trades table
CREATE TABLE trades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    crypto_pair_id BIGINT NOT NULL,
    trade_type VARCHAR(4) NOT NULL CHECK (trade_type IN ('BUY', 'SELL')),
    quantity DECIMAL(20,8) NOT NULL,
    price DECIMAL(20,8) NOT NULL,
    total_amount DECIMAL(20,8) NOT NULL,
    trade_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (crypto_pair_id) REFERENCES crypto_pairs(id)
);

-- User wallets table
CREATE TABLE user_wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol_id BIGINT NOT NULL,
    balance DECIMAL(20,8) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (symbol_id) REFERENCES symbols(id),
    CONSTRAINT unique_user_symbol UNIQUE (user_id, symbol_id)
);

-- Insert base symbols
INSERT INTO symbols (symbol, name, active) VALUES
('BTC', 'Bitcoin', TRUE),
('ETH', 'Ethereum', TRUE),
('USDT', 'Tether', TRUE);

-- Crypto prices table to store all price updates from scheduler
CREATE TABLE crypto_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crypto_pair_id BIGINT NOT NULL,
    bid_price DECIMAL(20,8) NOT NULL,
    ask_price DECIMAL(20,8) NOT NULL,
    bid_source VARCHAR(20) NOT NULL,
    ask_source VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (crypto_pair_id) REFERENCES crypto_pairs(id)
);

-- Insert all tradable crypto pairs
INSERT INTO crypto_pairs (base_symbol_id, quote_symbol_id, pair_name, active) VALUES
(1, 3, 'BTCUSDT', TRUE),
(2, 3, 'ETHUSDT', TRUE);

