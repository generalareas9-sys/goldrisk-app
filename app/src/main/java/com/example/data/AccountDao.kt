package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM trading_accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<TradingAccount>>

    @Query("SELECT * FROM trading_accounts WHERE isActive = 1 LIMIT 1")
    fun getActiveAccount(): Flow<TradingAccount?>

    @Query("SELECT * FROM trading_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: Long): TradingAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: TradingAccount): Long

    @Update
    suspend fun updateAccount(account: TradingAccount)

    @Delete
    suspend fun deleteAccount(account: TradingAccount)

    @Query("UPDATE trading_accounts SET isActive = CASE WHEN id = :accountId THEN 1 ELSE 0 END")
    suspend fun setActiveAccount(accountId: Long)

    @Query("SELECT COUNT(*) FROM trading_accounts")
    suspend fun getAccountCount(): Int
}
