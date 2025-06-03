package com.example.vent.com.example.vent.utils

import android.content.Context
import android.util.Base64
import com.example.vent.utils.SecurePrefs
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object AuthTokenProvider {

    fun getAccessToken(context: Context): String? {
        return SecurePrefs.getAccessToken(context)
    }

    fun getRefreshToken(context: Context): String? {
        return SecurePrefs.getRefreshToken(context)
    }

    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        SecurePrefs.saveTokens(context, accessToken, refreshToken)
    }

    fun clearTokens(context: Context) {
        SecurePrefs.clearTokens(context)
    }

    fun isAccessTokenExpired(context: Context): Boolean {
        val token = getAccessToken(context) ?: return true
        return isJwtExpired(token)
    }

    fun isRefreshTokenExpired(context: Context): Boolean {
        val token = getRefreshToken(context) ?: return true
        return isJwtExpired(token)
    }

    fun getAccessTokenExpiry(context: Context): Long? {
        return extractJwtExpiry(getAccessToken(context))
    }

    fun getRefreshTokenExpiry(context: Context): Long? {
        return extractJwtExpiry(getRefreshToken(context))
    }

    fun getBearerAccessToken(context: Context): String? {
        return getAccessToken(context)?.let { "Bearer $it" }
    }

    private fun isJwtExpired(token: String): Boolean {
        return try {
            val exp = extractJwtExpiry(token) ?: return true
            val currentTime = System.currentTimeMillis() / 1000
            currentTime >= exp
        } catch (e: Exception) {
            true
        }
    }

    private fun extractJwtExpiry(token: String?): Long? {
        return try {
            val parts = token?.split(".") ?: return null
            if (parts.size != 3 || parts.any { it.isBlank() }) return null

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8)
            val json = JSONObject(payload)
            json.optLong("exp", 0L).takeIf { it != 0L }
        } catch (e: Exception) {
            null
        }
    }
}
