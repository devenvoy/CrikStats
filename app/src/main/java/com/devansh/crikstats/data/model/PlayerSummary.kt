package com.devansh.crikstats.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerSummary(
    @SerialName("country") var country: String = "",
    @SerialName("id") var id: String = "",
    @SerialName("name") var name: String = ""
)