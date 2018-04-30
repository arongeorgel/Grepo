package com.aron.grepo.models

import com.google.gson.annotations.SerializedName

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
data class RepositoryModel(
        var name: String = "",
        var description: String = "",
        var isFavorite: Boolean = false,
        var lastUpdate: String? = ""
)
