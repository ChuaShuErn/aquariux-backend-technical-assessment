# Assumptions & Design Decisions

## Bugs Found and Fixed

### 1. V2 Seed Data — Jan 16 Pair IDs Swapped
While browsing through the seeded data in H2Database, I realised that some of the prices for BTC was showing ~$3100 and ETH was ~$46000. I do not think that was intentional, and could not find a good reason why we needed to swap prices. I took this as a test that I am to exercise Data Integrity.

**Fix:** Created `V5__Fix_swapped_pair_ids.sql` using a CASE-based UPDATE to swap the pair IDs for all records on Jan 16.

### 2. PriceUpdateScheduler — Bid/Ask/BidSource/AskSource Values Swapped
`storePriceHistory()` was setting `bidPrice` to `askPrice` and vice versa. The same problem applied to ask and bid sources. I could not find a good reason to swap it, because the deciding of whether we use `bidPrice` or `askPrice` in a `BUY` or `SELL` order should be determined at the Service layer, and not at the Data Ingestion layer. 

**Fix:** Corrected the four setter calls to use the correct parameter assignments.

### 3. PriceUpdateScheduler — Pair ID Lookup Swapped
`getCryptoPairId()` was swapping BTCUSDT to ETHUSDT and vice versa before looking up the pair ID. This caused BTC prices to be stored under the ETHUSDT pair and ETH prices under BTCUSDT. I could not find a good reason why that line existed, and I believe this is a mistake.

**Fix:** Removed the swap logic.


### 4. SchedulerConfig — @EnableScheduling Not Applied
The `SchedulerConfig` class imported `@EnableScheduling` but did not apply it, so the price update scheduler never ran.

**Fix:** Added `@EnableScheduling` annotation to the class.

## Trade Execution Design

### Market Order Convention
- **BUY** executes at the **ASK price** (lowest price a seller is willing to accept)
- **SELL** executes at the **BID price** (highest price a buyer is willing to pay)
- This follows standard market order convention where the trader takes liquidity from the opposite side of the order book


### Rounding
Rounding should favor the platform: CEILING for debit amounts (user pays more), FLOOR for credit amounts (user receives less).

### Wallet Management
- Wallets are created on first acquisition (e.g., user's first BTC purchase creates a BTC wallet)
- The debit wallet must exist and have sufficient balance before a trade is executed
- Wallet updates and trade insertion are wrapped in `@Transactional` for atomicity
- Added a `version` column to `user_wallets` for optimistic locking. On each update to the wallet, version is checked and incremented. In the scenario where two trades affect the same `wallet` simultaneously, the second one gets rejected. We check  for the return value `UserWalletMapper::updateBalance` to check how many rows have been updated.

### Validation
- Request validation (null checks, quantity > 0) is performed in the service layer
- Business validation (user exists, pair exists, price available, sufficient balance) follows after request validation
- `spring-boot-starter-validation` is not in the project dependencies, so Bean Validation annotations are not used — manual validation provides equivalent coverage

### Error Handling
- Custom exceptions with RFC 7807 ProblemDetail responses:
  - `ResourceNotFoundException` (404) — user, pair, or price not found
  - `InsufficientBalanceException` (400) — not enough balance to execute trade
  - `InvalidTradeRequestException` (400) — missing or invalid request fields

## Out of Scope (per Business Requirements)
- Transaction fees
- Advanced order types (limit, stop-loss)
- Trade cancellation
- Wallet-to-wallet transfers
- Real-time price streaming
