package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AccountCurrency
import com.example.model.RiskMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TradingAccount::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xauusd_calculator.db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default profiles: Exness Standard Cent & Exness Standard
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).accountDao()
                            dao.insertAccount(
                                TradingAccount(
                                    name = "Exness Standard Cent",
                                    brokerName = "Exness",
                                    symbol = "XAUUSDc",
                                    accountType = "Exness Standard Cent",
                                    balance = 1220.3,
                                    currency = AccountCurrency.USC.name,
                                    defaultRiskAmount = 100.0,
                                    defaultRiskPercentage = 1.0,
                                    riskMode = RiskMode.FIXED_AMOUNT.name,
                                    contractSize = 1.0,
                                    minLot = 0.01,
                                    lotStep = 0.01,
                                    maxLot = 100.0,
                                    isActive = true
                                )
                            )
                            dao.insertAccount(
                                TradingAccount(
                                    name = "Exness Standard",
                                    brokerName = "Exness",
                                    symbol = "XAUUSDm",
                                    accountType = "Exness Standard",
                                    balance = 1000.0,
                                    currency = AccountCurrency.USD.name,
                                    defaultRiskAmount = 20.0,
                                    defaultRiskPercentage = 1.0,
                                    riskMode = RiskMode.FIXED_AMOUNT.name,
                                    contractSize = 100.0,
                                    minLot = 0.01,
                                    lotStep = 0.01,
                                    maxLot = 200.0,
                                    isActive = false
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
