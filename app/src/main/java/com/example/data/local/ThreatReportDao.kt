package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatReportDao {
  @Query("SELECT * FROM threat_reports ORDER BY timestamp DESC")
  fun getAllReports(): Flow<List<ThreatReportEntity>>

  @Query("SELECT * FROM threat_reports WHERE id = :id LIMIT 1")
  suspend fun getReportById(id: String): ThreatReportEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReport(report: ThreatReportEntity)

  @Update
  suspend fun updateReport(report: ThreatReportEntity)

  @Delete
  suspend fun deleteReport(report: ThreatReportEntity)

  @Query("DELETE FROM threat_reports")
  suspend fun deleteAll()

  @Query("SELECT COUNT(*) FROM threat_reports")
  suspend fun getReportCount(): Int
}
