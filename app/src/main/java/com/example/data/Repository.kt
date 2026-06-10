package com.example.data

import kotlinx.coroutines.flow.Flow

class DuniaRepository(private val dao: DuniaDao) {

    // Configs
    val allConfigs: Flow<List<DuniaConfig>> = dao.getAllConfigs()
    suspend fun insertConfig(config: DuniaConfig) = dao.insertConfig(config)

    // Transactions
    val allTransactions: Flow<List<ItemTransaction>> = dao.getAllTransactionsFlow()
    suspend fun insertTransaction(transaction: ItemTransaction) = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: ItemTransaction) = dao.deleteTransaction(transaction)
    suspend fun deleteTransactionById(id: Int) = dao.deleteTransactionById(id)

    // Savings Goals
    val allSavingsGoals: Flow<List<SavingGoal>> = dao.getAllSavingsGoalsFlow()
    suspend fun insertSavingGoal(goal: SavingGoal) = dao.insertSavingGoal(goal)
    suspend fun updateSavingGoal(goal: SavingGoal) = dao.updateSavingGoal(goal)

    // Investments
    val allInvestments: Flow<List<InvestmentItem>> = dao.getAllInvestmentsFlow()
    suspend fun insertInvestment(investment: InvestmentItem) = dao.insertInvestment(investment)
    suspend fun deleteInvestment(investment: InvestmentItem) = dao.deleteInvestment(investment)

    // Installments
    val allInstallments: Flow<List<InstallmentItem>> = dao.getAllInstallmentsFlow()
    suspend fun insertInstallment(installment: InstallmentItem) = dao.insertInstallment(installment)
    suspend fun updateInstallment(installment: InstallmentItem) = dao.updateInstallment(installment)
    suspend fun deleteInstallment(installment: InstallmentItem) = dao.deleteInstallment(installment)

    // Roadmap Phases
    val allRoadmapPhases: Flow<List<RoadmapPhase>> = dao.getAllRoadmapPhasesFlow()
    suspend fun insertRoadmapPhase(phase: RoadmapPhase) = dao.insertRoadmapPhase(phase)
    suspend fun updateRoadmapPhase(phase: RoadmapPhase) = dao.updateRoadmapPhase(phase)

    // Ibadah Tracker
    val allIbadahRecords: Flow<List<IbadahRecord>> = dao.getAllIbadahRecordsFlow()
    suspend fun getIbadahRecordByDate(date: String): IbadahRecord? = dao.getIbadahRecordByDate(date)
    suspend fun insertIbadahRecord(record: IbadahRecord) = dao.insertIbadahRecord(record)

    // Meetings
    val allMeetings: Flow<List<RapatNotulen>> = dao.getAllMeetingsFlow()
    suspend fun insertMeeting(meeting: RapatNotulen) = dao.insertMeeting(meeting)
    suspend fun updateMeeting(meeting: RapatNotulen) = dao.updateMeeting(meeting)
    suspend fun deleteMeeting(meeting: RapatNotulen) = dao.deleteMeeting(meeting)

    // Wishlist
    val allWishlist: Flow<List<WishlistItem>> = dao.getAllWishlistFlow()
    suspend fun insertWishlistItem(item: WishlistItem) = dao.insertWishlistItem(item)
    suspend fun updateWishlistItem(item: WishlistItem) = dao.updateWishlistItem(item)
    suspend fun deleteWishlistItem(item: WishlistItem) = dao.deleteWishlistItem(item)
}
