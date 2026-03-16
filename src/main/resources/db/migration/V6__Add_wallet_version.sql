-- Optimistic locking: prevents lost updates from concurrent wallet modifications
ALTER TABLE user_wallets ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;
