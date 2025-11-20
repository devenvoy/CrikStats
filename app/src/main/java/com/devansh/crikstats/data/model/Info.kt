package com.devansh.crikstats.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Info(
    @SerialName("cache") var cache: Int = 0,
    @SerialName("credits") var credits: Int = 0,
    @SerialName("hitsLimit") var hitsLimit: Int = 0,
    @SerialName("hitsToday") var hitsToday: Int = 0,
    @SerialName("hitsUsed") var hitsUsed: Int = 0,
    @SerialName("offsetRows") var offsetRows: Int = 0,
    @SerialName("queryTime") var queryTime: Double = 0.0,
    @SerialName("s") var s: Int = 0,
    @SerialName("server") var server: Int = 0,
    @SerialName("totalRows") var totalRows: Int = 0
)