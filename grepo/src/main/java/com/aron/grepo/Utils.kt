package com.aron.grepo

/**
 * @author Georgel Aron
 * @since 01/05/2018
 * @version 1.0.0
 */

fun <T> Any.unsafeListCast() : List<T> {
    @Suppress("UNCHECKED_CAST")
    return this as List<T>
}