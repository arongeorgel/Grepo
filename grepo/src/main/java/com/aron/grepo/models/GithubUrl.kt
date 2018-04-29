package com.aron.grepo.models

import java.net.URL

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */

data class GithubUrl(val httpUrl: String, val rel: UrlRel)

enum class UrlRel(val value: String) {
    NEXT("next"), PREV("prev"), LAST("last"), FIRST("prev"), UNKNOWN("unknown");

    internal companion object {
        fun fromString(value: String): UrlRel {
            for (rel: UrlRel in values()) {
                if (rel.value == value) {
                    return rel
                }
            }
            return UNKNOWN
        }
    }
}