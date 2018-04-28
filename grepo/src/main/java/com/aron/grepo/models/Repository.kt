package com.aron.grepo.models

import com.google.gson.annotations.SerializedName

/**
 * @author Georgel Aron
 * @since 28/04/2018
 * @version 1.0.0
 */
data class Repository(
        val id: String,
        val name: String,
        val description: String,
        @SerializedName("updated_at") val lastUpdate: String
)