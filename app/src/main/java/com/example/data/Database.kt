package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. CONFIG ENTITY
@Entity(tableName = "dunia_config")
data class DuniaConfig(
    @PrimaryKey val key: String,
    val value: String
)

// 2. TRANSACTION ENTITY (Combines Pemasukan & Pengeluaran)
@Entity(tableName = "transactions")
data class ItemTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // "Haikal" or "Ummu" or "Bersama"
    val type: String, // "Pemasukan" or "Pengeluaran"
    val category: String, // "Makan", "Transport", "Pulsa", "Gaji", etc.
    val pos: String, // "Wajib", "Penting", "Fleksibel", "Investasi", "Gaji", "Sampingan"
    val amount: Double,
    val description: String,
    val timestamp: Long,
    val tag: String = "", // e.g., "#darurat", "#rutin", "#impulsif"
    val receiptPath: String? = null
)

// 3. SAVINGS GOALS ENTITY
@Entity(tableName = "savings_goals")
data class SavingGoal(
    @PrimaryKey val id: String, // "darurat_haikal", "darurat_ummu", "darurat_gabungan", "nikah", "dp_rumah", "umum_haikal", "umum_ummu", "liburan"
    val name: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val description: String
)

// 4. INVESTMENT ENTITY
@Entity(tableName = "investments")
data class InvestmentItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Emas", "Reksa Dana", "Saham", "Deposito", "P2P", "Kripto"
    val name: String,
    val capital: Double,
    val currentValue: Double,
    val notes: String = ""
)

// 5. INSTALLMENT ENTITY
@Entity(tableName = "installments")
data class InstallmentItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // "Haikal" or "Ummu"
    val name: String,
    val monthlyPayment: Double,
    val remainingMonths: Int,
    val dueDate: Int, // Day of month
    val totalAmount: Double,
    val amountPaid: Double
)

// 6. ROADMAP ENTITY
@Entity(tableName = "roadmap_phases")
data class RoadmapPhase(
    @PrimaryKey val phaseId: Int, // 1, 2, 3, 4, 5
    val title: String, // "Santai", "Fondasi", "Akselerasi", "Komitmen", "Keluarga"
    val year: String, // "2026", "2027", "2028", "2029", "2030"
    val description: String,
    val isCompleted: Boolean,
    val milestones: String, // Semi-colon separated: "Rencana A; Rencana B"
    val completedMilestones: String // Semi-colon separated indices: "0;1"
)

// 7. IBADAH DAILY TRACKER ENTITY
@Entity(tableName = "ibadah_daily")
data class IbadahRecord(
    @PrimaryKey val date: String, // format: "yyyy-MM-dd"
    val subuh: Boolean = false,
    val zhuhur: Boolean = false,
    val ashar: Boolean = false,
    val maghrib: Boolean = false,
    val isya: Boolean = false,
    val sedekahAmount: Double = 0.0,
    val quranPages: Int = 0
)

// 8. MEETING MINUTES ENTITY
@Entity(tableName = "group_meetings")
data class RapatNotulen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthYear: String, // "Juni 2026"
    val date: String, // "2026-06-10"
    val agenda: String,
    val notes: String,
    val actionItems: String, // Semi-colon separated e.g. "Assign Haikal: Nabung Rp 500rb"
    val completedActions: String // Semi-colon separated indices "0;3"
)

// 9. WISHLIST ENTITY
@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // "Haikal" or "Ummu"
    val name: String,
    val price: Double,
    val addedDate: Long,
    val status: String, // "Belum 30 Hari", "Sudah 30 Hari", "Dibeli", "Dibatalkan"
    val notes: String = ""
)

// DAO INTERFACE
@Dao
interface DuniaDao {
    // Configuration
    @Query("SELECT * FROM dunia_config")
    fun getAllConfigs(): Flow<List<DuniaConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: DuniaConfig)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<ItemTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: ItemTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: ItemTransaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    // Savings Goals
    @Query("SELECT * FROM savings_goals")
    fun getAllSavingsGoalsFlow(): Flow<List<SavingGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingGoal(goal: SavingGoal)

    @Update
    suspend fun updateSavingGoal(goal: SavingGoal)

    // Investments
    @Query("SELECT * FROM investments")
    fun getAllInvestmentsFlow(): Flow<List<InvestmentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentItem)

    @Delete
    suspend fun deleteInvestment(investment: InvestmentItem)

    // Installments
    @Query("SELECT * FROM installments")
    fun getAllInstallmentsFlow(): Flow<List<InstallmentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(installment: InstallmentItem)

    @Update
    suspend fun updateInstallment(installment: InstallmentItem)

    @Delete
    suspend fun deleteInstallment(installment: InstallmentItem)

    // Roadmap Phases
    @Query("SELECT * FROM roadmap_phases ORDER BY phaseId ASC")
    fun getAllRoadmapPhasesFlow(): Flow<List<RoadmapPhase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmapPhase(phase: RoadmapPhase)

    @Update
    suspend fun updateRoadmapPhase(phase: RoadmapPhase)

    // Ibadah Tracker
    @Query("SELECT * FROM ibadah_daily")
    fun getAllIbadahRecordsFlow(): Flow<List<IbadahRecord>>

    @Query("SELECT * FROM ibadah_daily WHERE date = :date LIMIT 1")
    suspend fun getIbadahRecordByDate(date: String): IbadahRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIbadahRecord(record: IbadahRecord)

    // Meetings
    @Query("SELECT * FROM group_meetings ORDER BY id DESC")
    fun getAllMeetingsFlow(): Flow<List<RapatNotulen>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: RapatNotulen)

    @Update
    suspend fun updateMeeting(meeting: RapatNotulen)

    @Delete
    suspend fun deleteMeeting(meeting: RapatNotulen)

    // Wishlist
    @Query("SELECT * FROM wishlist_items ORDER BY addedDate DESC")
    fun getAllWishlistFlow(): Flow<List<WishlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItem)

    @Update
    suspend fun updateWishlistItem(item: WishlistItem)

    @Delete
    suspend fun deleteWishlistItem(item: WishlistItem)
}

// DATABASE CLASS
@Database(
    entities = [
        DuniaConfig::class,
        ItemTransaction::class,
        SavingGoal::class,
        InvestmentItem::class,
        InstallmentItem::class,
        RoadmapPhase::class,
        IbadahRecord::class,
        RapatNotulen::class,
        WishlistItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DuniaDatabase : RoomDatabase() {
    abstract fun duniaDao(): DuniaDao
}
