package com.devansh.crikstats.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfoStats(
    @SerialName("fn") var fn: String = "",
    @SerialName("matchtype") var matchtype: String = "",
    @SerialName("stat") var stat: String = "",
    @SerialName("value") var value: String = ""
)