package com.devansh.crikstats.data.model

data class PlayerStatsResponse(
    val id: String,
    val name: String,
    val country: String,
    val role: String?=null,
    val battingStyle: String?=null,
    val bowlingStyle: String?=null,
    val dateOfBirth: String?=null,
    val placeOfBirth: String?=null,
    val playerImg: String? = null,
    val stats: List<PlayerInfoStats>? = null
)
