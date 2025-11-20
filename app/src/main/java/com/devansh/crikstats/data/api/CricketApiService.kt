package com.devansh.crikstats.data.api

import com.devansh.crikstats.BuildConfig
import com.devansh.crikstats.data.model.PlayerStatsResponse
import com.devansh.crikstats.data.model.PlayerSummary
import com.devansh.crikstats.data.model.PlayersApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CricketApiService {

    @GET("/v1/players_info")
    suspend fun getPlayerStats(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("id") playerId: String
    ): Response<PlayersApiResponse<PlayerStatsResponse>>

    @GET("v1/players")
    suspend fun getPlayers(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("offset") offset: Int = 0
    ): Response<PlayersApiResponse<List<PlayerSummary>>>

    companion object {
        const val BASE_URL = "https://api.cricapi.com/"
        const val API_KEY = BuildConfig.API_KEY
    }
}