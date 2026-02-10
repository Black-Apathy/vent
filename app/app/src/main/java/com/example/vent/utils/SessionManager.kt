package com.example.vent.com.example.vent.utils

import android.content.Context
import android.util.Base64
import com.example.vent.utils.SecurePrefs
import org.json.JSONObject
import java.nio.charset.Charset

object SessionManager {

    private fun getDecodedPayload(token: String?): JSONObject? {
        if (token.isNullOrEmpty()) return null
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payloadJson = String(Base64.decode(parts[1], Base64.DEFAULT), Charset.forName("UTF-8"))
            JSONObject(payloadJson)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserRole(context: Context): String? {
        val token = SecurePrefs.getAccessToken(context)
        val payload = getDecodedPayload(token)
        return payload?.optString("role") // Returns "admin", "teacher", etc.
    }

    fun getUserName(context: Context): String? {
        val token = SecurePrefs.getAccessToken(context)
        val payload = getDecodedPayload(token)
        return payload?.optString("name") // Returns the user's name
    }

    fun isTokenExpired(token: String): Boolean {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return true // malformed token

            val payloadJson = String(Base64.decode(parts[1], Base64.DEFAULT), Charset.forName("UTF-8"))
            val payload = JSONObject(payloadJson)
            val exp = payload.getLong("exp") * 1000  // Convert to milliseconds
            return System.currentTimeMillis() > exp
        } catch (e: Exception) {
            return true
        }
    }

    fun isLoggedIn(context: Context): Boolean {
        val accessToken = SecurePrefs.getAccessToken(context)
        return !accessToken.isNullOrEmpty() && !isTokenExpired(accessToken)
    }

    fun shouldForceLogout(context: Context): Boolean {
        val token = SecurePrefs.getAccessToken(context)
        return token.isNullOrEmpty() || isTokenExpired(token)
    }

    fun logout(context: Context) {
        SecurePrefs.clearTokens(context)
    }

}
