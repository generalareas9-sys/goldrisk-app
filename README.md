# GoldRisk 📱

GoldRisk is a mobile trading position-size calculator focused on **XAUUSD (Gold)** trading.

The main purpose of GoldRisk is to help traders quickly calculate the appropriate lot size for a trade based on the amount of money or percentage they are willing to risk.

## 🎯 Main Purpose

When trading Gold, calculating the correct position size manually can take time. GoldRisk simplifies the process:

**Account Balance + Risk + Entry Price + Stop Loss → Recommended Lot Size**

This allows the trader to determine the position size before executing a trade.

## 🥇 Primary Market

The initial focus of GoldRisk is:

- **Instrument:** XAUUSD / Gold
- **Exness Standard Cent Account**
- **Symbol:** XAUUSDc
- **Contract Size:** 1
- **Account Currency:** USC
- **100 USC = 1 USD**

The application is designed so that broker/account specifications can be handled explicitly rather than assuming that all trading instruments use the same contract specifications.

## 🧮 Calculator Inputs

The user can provide:

- Account type
- Account balance
- Risk amount
- Risk percentage
- Position type (BUY / SELL)
- Entry price
- Stop Loss price
- Take Profit price (optional)

## 📊 Calculator Outputs

GoldRisk calculates:

- Stop-Loss Distance
- Exact Lot Size
- Recommended Lot Size
- Risk Amount
- Risk-to-Reward Ratio (when Take Profit is provided)
- Potential Profit (when Take Profit is provided)

### Example

**Account**

- Account Type: Exness Standard Cent
- Balance: 1,220.3 USC
- Risk: 100 USC = $1.00
- Contract Size: 1
- Symbol: XAUUSDc

**Trade**

- Position: BUY
- Entry: 4014.73
- Stop Loss: 4002.69
- Take Profit: 4052.23

**Result**

- Stop-Loss Distance: 12.04
- Exact Lot Size: 0.0831
- Recommended Lot Size: 0.08 lot
- Risk: 100 USC = $1.00
- Risk-to-Reward: 1 : 3.11
- Potential Profit: approximately 311 USC = $3.11

## 🚀 Features

- Fast Gold lot-size calculation
- Risk-based position sizing
- Percentage-based risk calculation
- Fixed monetary risk calculation
- BUY and SELL support
- Optional Take Profit calculation
- Risk-to-Reward calculation
- Potential profit calculation
- XAUUSD-focused workflow
- Simple mobile interface

## 🛠️ Technology

GoldRisk is currently being developed as a native Android application using:

- Kotlin
- Jetpack Compose
- Gradle
- Android SDK

The project is being developed with future support for a broader mobile deployment strategy in mind.

## 📱 Development

This project was initially generated/prototyped using Google AI Studio and is being developed further as a standalone mobile application.

## ⚠️ Disclaimer

GoldRisk is a calculation tool and does not provide financial advice.

Users are responsible for verifying broker specifications, account conditions, prices, and calculated position sizes before placing a trade.

## 📌 Project Status

🚧 **In Development**

The current development focus is the XAUUSD Gold lot-size calculator and its calculation accuracy, usability, and mobile user experience.
