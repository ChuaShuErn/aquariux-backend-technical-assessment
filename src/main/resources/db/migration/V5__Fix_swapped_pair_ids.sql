-- Fix: Jan 16 price data had crypto_pair_id swapped

UPDATE crypto_prices
SET crypto_pair_id = CASE
    WHEN crypto_pair_id = 1 THEN 2
    WHEN crypto_pair_id = 2 THEN 1
END
WHERE created_at >= '2025-01-16 00:00:00'
  AND created_at < '2025-01-17 00:00:00';
