package com.devansh.core.data.repository

import com.devansh.core.data.api.CricketApiService
import com.devansh.core.data.model.PlayerStatsResponse
import com.devansh.core.data.model.PlayerSummary
import com.devansh.core.utils.exceptions.RepositoryException
import javax.inject.Inject

interface CricketRepository {
    suspend fun getPlayerStats(playerId: String): Result<PlayerStatsResponse>
    suspend fun getPlayers(): List<PlayerSummary>
}


class CricketRepositoryImpl @Inject constructor(
    private val apiService: CricketApiService
) : CricketRepository {


    override suspend fun getPlayerStats(playerId: String): Result<PlayerStatsResponse> {
        return try {
            val response = apiService.getPlayerStats(playerId = playerId)
            if (response.isSuccessful) {
                val result = response.body()?.data
                    ?: return Result.failure(Exception("Failed to fetch player stats"))
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to fetch player stats"))
            }
        } catch (_: Exception) {
            Result.failure(Exception("Failed to fetch player stats"))
        }
    }

    override suspend fun getPlayers(): List<PlayerSummary> {
        return try {
            val response = apiService.getPlayers()
            if (response.isSuccessful) {
                val result = response.body()?.data
                    ?: return emptyList()
                return result.map { player ->
                    PlayerSummary(
                        id = player.id,
                        name = player.name,
                        country = player.country,
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            throw RepositoryException("Failed to fetch players: ${e.message}", e)
        }
    }
}