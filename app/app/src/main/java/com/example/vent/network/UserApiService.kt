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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object UserApiService {

    fun signupUser(context: Context, email: String, password: String, onResult: (Boolean) -> Unit) {
        val url = ApiConstants.LOGIN_URL
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response here
                // You can parse the response if needed
                Log.d("LoginResponse", response)

                // If login is successful, return true
                onResult(true)
            },
            Response.ErrorListener { error ->
                // Log basic error message
                Log.e("LoginError", "Error: ${error.message}")

                // Log cause if available
                Log.e("LoginError", "Error Cause: ${error.cause?.message}")

                // Log network response details if available
                error.networkResponse?.let { networkResponse ->
                    Log.e("LoginError", "Status Code: ${networkResponse.statusCode}")
                    Log.e("LoginError", "Response Data: ${String(networkResponse.data)}")
                }

                // In case of a network timeout or other unknown errors
                if (error is TimeoutError) {
                    Log.e("LoginError", "Network Timeout")
                } else if (error is NoConnectionError) {
                    Log.e("LoginError", "No Connection")
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

    fun fetchPendingUsers(context: Context, onSuccess: (List<User>) -> Unit, onError: (String) -> Unit) {
        val url = ApiConstants.FETCH_PENDING_USERS_URL

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                try {
                    val userList = mutableListOf<User>()
                    for (i in 0 until response.length()) {
                        val userJson: JSONObject = response.getJSONObject(i)
                        val user = User(
                            id = userJson.getInt("request_id"),
                            email = userJson.getString("email"),
                            password = userJson.getString("password_hash"),
                            createdAt = userJson.getString("request_date"),
                        )
                        userList.add(user)
                    }
                    onSuccess(userList)
                } catch (e: Exception) {
                    onError("Parsing error: ${e.message}")
                }
            },
            { error ->
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
        )

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun acceptUser(context: Context?, requestId: Int, role: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url = ApiConstants.ACCEPT_USER_URL

        val params = JSONObject().apply {
            put("request_id", requestId)
            put("role", role)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                try {
                    val message = response.getString("message")  // Backend sends message
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
        )

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }

    fun rejectUser(context: Context?, requestId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url = ApiConstants.REJECT_USER_URL

        val jsonBody = JSONObject().apply {
            put("request_id", requestId)
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                onSuccess()
            },
            { error ->
                onError("Failed to reject: ${error.message ?: "Unknown error"}")
            }
        )

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }
}
