package entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entity(
    @SerialName("politerMessage")
    val politerMessage: String,
    @SerialName("provider")
    val provider: String? = null,
    @SerialName("model")
    val model: String? = null,
)