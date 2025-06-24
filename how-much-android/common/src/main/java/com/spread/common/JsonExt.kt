package com.spread.common

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}


// Extensions

fun JsonObject.optString(key: String, default: String? = null): String? {
    val value = this[key] ?: return default
    if (value is JsonPrimitive && value.isString) {
        return value.content
    }
    return default
}

fun JsonObject.optStringNotNull(key: String, default: String): String {
    val value = this[key] ?: return default
    if (value is JsonPrimitive && value.isString) {
        return value.content
    }
    return default
}

fun JsonObject.optDouble(key: String, default: Double = 0.0): Double {
    val value = this[key] ?: return default
    if (value is JsonPrimitive) {
        return value.doubleOrNull ?: default
    }
    return default
}