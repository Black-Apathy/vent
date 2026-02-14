package com.example.vent.network

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.vent.User
import com.example.vent.com.example.vent.utils.AuthTokenProvider
import com.example.vent.com.example.vent.utils.SessionManager
import com.example.vent.utils.SecurePrefs
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object UserApiService {

    fun viewEvents(context: Context, onResult: (Boolean, String) -> Unit) {
        val url = ApiConstants.VIEW_EVENTS

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    onResult(true, jsonArray.toString())
                } catch (e: JSONException) {
                    Log.e("ViewEvents", "JSON parsing error", e)
                    onResult(false, "Invalid response format")
                }
            },
            { error ->
                Log.e("ViewEvents", "Network error", error)
                onResult(false, "Network error: ${error.message}")
            }
        )

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun checkUserStatus(context: Context, email: String, onResult: (String, String) -> Unit) {
        val url = "${ApiConstants.CHECK_STATUS_URL}?email=$email"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    val status = json.optString("status", "pending")
                    val role = json.optString("role", "unknown")
                    onResult(status, role)
                } catch (e: JSONException) {
                    Log.e("StatusCheck", "JSON parsing error", e)
                    onResult("error", "unknown")
                }
            },
            { error ->
                Log.e("StatusCheck", "Error fetching status", error)
                onResult("error", "unknown")
            }
        )
        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun signupUser(context: Context, email: String, password: String, onResult: (Boolean) -> Unit) {
        val url = ApiConstants.REGISTER_URL
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response here
                // You can parse the response if needed
                Log.d("SignUpResponse", response)

                // If login is successful, return true
                onResult(true)
            },
            Response.ErrorListener { error ->
                // Log basic error message
                Log.e("SignUpError", "Error: ${error.message}")

                // Log cause if available
                Log.e("SignUpError", "Error Cause: ${error.cause?.message}")

                // Log network response details if available
                error.networkResponse?.let { networkResponse ->
                    Log.e("SignUpError", "Status Code: ${networkResponse.statusCode}")
                    Log.e("SignUpError", "Response Data: ${String(networkResponse.data)}")
                }

                // In case of a network timeout or other unknown errors
                if (error is TimeoutError) {
                    Log.e("SignUpError", "Network Timeout")
                } else if (error is NoConnectionError) {
                    Log.e("SignUpError", "No Connection")
                }

                // Return failure result
                onResult(false)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = mutableMapOf<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        // Adding the request to the queue
        VolleyHelper.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun loginUser(context: Context, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val url = ApiConstants.LOGIN_URL

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val message = jsonResponse.getString("message")

                    if (jsonResponse.has("accessToken") && jsonResponse.has("refreshToken")) {
                        val accessToken = jsonResponse.getString("accessToken")
                        val refreshToken = jsonResponse.getString("refreshToken")
                        val role = jsonResponse.optString("role")

                        AuthTokenProvider.saveTokens(context, accessToken, refreshToken)

                        Log.d("LoginSuccess", "Message: $message")
                        Log.d("LoginSuccess", "AccessToken: $accessToken")
                        Log.d("LoginSuccess", "RefreshToken: $refreshToken")
                        Log.d("LoginSuccess", "Role: $role")

                        // Return true + token/role info
                        onResult(true, "Role: $role")
                    } else {
                        Log.d("LoginFailure", "Login failed with message: $message")
                        onResult(false, message)
                    }
                } catch (e: Exception) {
                    Log.e("LoginError", "JSON parsing error: ${e.message}")
                    onResult(false, "Invalid response from server")
                }
            },
            Response.ErrorListener { error ->
                Log.e("LoginError", "Error: ${error.message}")
                error.networkResponse?.let { networkResponse ->
                    val statusCode = networkResponse.statusCode
                    val data = String(networkResponse.data)
                    Log.e("LoginError", "Status Code: $statusCode")
                    Log.e("LoginError", "Response Data: $data")

                    val errorMsg = try {
                        JSONObject(data).getString("message")
                    } catch (e: Exception) {
                        "Unexpected error"
                    }

                    val userFriendlyMsg = when (statusCode) {
                        400 -> "Email and password required"
                        401 -> "Invalid credentials"
                        403 -> "User not approved"
                        404 -> "User not found"
                        500 -> "Server error"
                        else -> errorMsg
                    }

                    onResult(false, userFriendlyMsg)
                } ?: run {
                    if (error is TimeoutError) {
                        onResult(false, "Network timeout")
                    } else if (error is NoConnectionError) {
                        onResult(false, "No internet connection")
                    } else {
                        onResult(false, "Unknown error")
                    }
                }
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "email" to email,
                    "password" to password
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Content-Type" to "application/x-www-form-urlencoded")
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun resetPassword(context: Context, email: String, newPassword: String, onResult: (Boolean, String) -> Unit) {
        val url = ApiConstants.RESET_PASSWORD_URL

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.optBoolean("success", false)
                    val message = jsonResponse.optString("message", "No message")

                    if (success) {
                        Log.d("ResetPassword", "Password reset successful: $message")
                        onResult(true, message)
                    } else {
                        Log.e("ResetPassword", "Password reset failed: $message")
                        onResult(false, message)
                    }
                } catch (e: Exception) {
                    Log.e("ResetPassword", "JSON parsing error: ${e.message}")
                    onResult(false, "Invalid response from server")
                }
            },
            Response.ErrorListener { error ->
                Log.e("ResetPassword", "Error: ${error.message}")
                error.networkResponse?.let { networkResponse ->
                    val statusCode = networkResponse.statusCode
                    val data = String(networkResponse.data)

                    val errorMsg = try {
                        JSONObject(data).getString("message")
                    } catch (e: Exception) {
                        "Unexpected error"
                    }

                    val userFriendlyMsg = when (statusCode) {
                        400 -> "Email and new password required"
                        404 -> "User not found"
                        500 -> "Server error"
                        else -> errorMsg
                    }

                    onResult(false, userFriendlyMsg)
                } ?: run {
                    if (error is TimeoutError) {
                        onResult(false, "Network timeout")
                    } else if (error is NoConnectionError) {
                        onResult(false, "No internet connection")
                    } else {
                        onResult(false, "Unknown error")
                    }
                }
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "email" to email,
                    "newPassword" to newPassword
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Content-Type" to "application/x-www-form-urlencoded")
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun fetchPendingUsers(context: Context, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        if (SessionManager.shouldForceLogout(context)) {
            onError("Session expired. Please log in again.")
            return
        }

        val url = ApiConstants.FETCH_PENDING_USERS_URL

        // ✅ Get the secure access token
        val accessToken = AuthTokenProvider.getAccessToken(context)

        if (accessToken.isNullOrEmpty()) {
            onError("Access token is missing. Please log in again.")
            return
        }

        val request = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val userList = mutableListOf<User>()
                    for (i in 0 until response.length()) {
                        val userJson = response.getJSONObject(i)
                        val user = User(
                            id = userJson.getInt("request_id"),
                            email = userJson.getString("email"),
                            password = userJson.getString("password_hash"),
                            createdAt = userJson.getString("request_date")
                        )
                        userList.add(user)
                    }
                    onSuccess(userList)
                } catch (e: Exception) {
                    onError("Parsing error: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                val errorMessage = when (error) {
                    is TimeoutError -> "Request timed out. Please check your internet connection."
                    is NoConnectionError -> "No internet connection. Please try again later."
                    is AuthFailureError -> "Authentication failed. Please contact support."
                    is ServerError -> "Server error. Please try again later."
                    is NetworkError -> "A network error occurred. Please check your connection."
                    is ParseError -> "Data parsing error. Please contact support."
                    else -> "Unexpected error: ${error.message}"
                }
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun getPendingCount(context: Context, onResult: (Int) -> Unit) {
        val url = ApiConstants.FETCH_PENDING_USERS_URL
        val accessToken = AuthTokenProvider.getAccessToken(context)

        val request = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response ->
                onResult(response.length()) // Simply return the size of the array
            },
            Response.ErrorListener {
                onResult(0) // Default to 0 on error to hide the dot
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }
        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun fetchAllUsers(context: Context, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        if (SessionManager.shouldForceLogout(context)) {
            onError("Session expired. Please log in again.")
            return
        }

        val url = ApiConstants.FETCH_ALL_USERS_URL

        // ✅ Get the secure access token
        val accessToken = AuthTokenProvider.getAccessToken(context)

        if (accessToken.isNullOrEmpty()) {
            onError("Access token is missing. Please log in again.")
            return
        }

        val request = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val userList = mutableListOf<User>()
                    for (i in 0 until response.length()) {
                        val userJson = response.getJSONObject(i)

                        val user = User(
                            id = userJson.optInt("user_id", 0),
                            email = userJson.optString("email", "Unknown"),
                            role = userJson.optString("role", "student"),
                            status = userJson.optString("approved_date", "approved_date"),
                            password = "",
                            createdAt = userJson.optString("request_date", "")
                        )
                        userList.add(user)
                    }
                    onSuccess(userList)
                } catch (e: Exception) {
                    onError("Parsing error: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                val errorMessage = when (error) {
                    is TimeoutError -> "Request timed out. Please check your internet connection."
                    is NoConnectionError -> "No internet connection. Please try again later."
                    is AuthFailureError -> "Authentication failed. Please contact support."
                    is ServerError -> "Server error. Please try again later."
                    is NetworkError -> "A network error occurred. Please check your connection."
                    is ParseError -> "Data parsing error. Please contact support."
                    else -> "Unexpected error: ${error.message}"
                }
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun acceptUser(context: Context?, requestId: Int, role: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (context == null || SessionManager.shouldForceLogout(context)) {
            onError("Session expired. Please log in again.")
            return
        }

        val url = ApiConstants.ACCEPT_USER_URL

        val params = JSONObject().apply {
            put("request_id", requestId)
            put("role", role)
        }

        val request = object : JsonObjectRequest(
            Method.POST, url, params,
            { response ->
                try {
                    val message = response.getString("message")
                    if (message.contains("User approved", ignoreCase = true)) {
                        onSuccess()
                    } else {
                        onError("Failed to accept user: $message")
                    }
                } catch (e: JSONException) {
                    onError("Response parsing error: ${e.message}")
                }
            },
            { error ->
                onError("Network error: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                val bearerToken = AuthTokenProvider.getBearerAccessToken(context)
                if (bearerToken != null) {
                    headers["Authorization"] = bearerToken
                }
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun rejectUser(context: Context?, requestId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (context == null || SessionManager.shouldForceLogout(context)) {
            onError("Session expired. Please log in again.")
            return
        }

        val url = ApiConstants.REJECT_USER_URL

        val jsonBody = JSONObject().apply {
            put("request_id", requestId)
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            jsonBody,
            { response ->
                onSuccess()
            },
            { error ->
                onError("Failed to reject: ${error.message ?: "Unknown error"}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                val bearerToken = AuthTokenProvider.getBearerAccessToken(context)
                if (bearerToken != null) {
                    headers["Authorization"] = bearerToken
                }
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun refreshToken(context: Context, refreshToken: String, onResult: (Boolean) -> Unit) {
        val stringRequest = object : StringRequest(
            Method.POST, ApiConstants.REFRESH_TOKEN_URL,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.optBoolean("success", false)
                    if (success && jsonResponse.has("accessToken")) {
                        val accessToken = jsonResponse.getString("accessToken")
                        val newRefreshToken = jsonResponse.optString("refreshToken", refreshToken)

                        AuthTokenProvider.saveTokens(context, accessToken, newRefreshToken)
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                } catch (e: Exception) {
                    onResult(false)
                }
            },
            Response.ErrorListener {
                onResult(false)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf("refreshToken" to refreshToken)
            }

            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Content-Type" to "application/x-www-form-urlencoded")
            }
        }

        VolleyHelper.getInstance(context).addToRequestQueue(stringRequest)
    }

}
