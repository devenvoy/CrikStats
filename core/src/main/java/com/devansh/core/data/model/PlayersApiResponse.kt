package com.devansh.core.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayersApiResponse<T>(
    @SerialName("apikey") var apikey: String = "",
    @SerialName("data") var data: T? = null,
    @SerialName("info") var info: Info = Info(),
    @SerialName("status") var status: String = ""
)
