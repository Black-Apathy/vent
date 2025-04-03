package com.example.vent.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.vent.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object UserApiService {

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
                onError("Network error: ${error.message}")
            }
        )

        VolleyHelper.getInstance(context).addToRequestQueue(request)
    }
    fun acceptUser(context: Context?, email: String, password: String, role: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url = ApiConstants.ACCEPT_USER_URL

        val params = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("role", role)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                try {
                    if (response.getBoolean("success")) {
                        onSuccess()
                    } else {
                        onError("Failed to accept user")
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

//    fun rejectUser(context: Context, email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
//        val url = ApiConstants.REJECT_USER_URL
//
//        val request = object : StringRequest(
//            Method.POST, url,
//            { response ->
//                if (response == "success") {
//                    onSuccess()
//                } else {
//                    onError("Failed to reject user")
//                }
//            },
//            { error ->
//                onError("Network error: ${error.message}")
//            }
//        ) {
//            override fun getParams(): MutableMap<String, String> {
//                return hashMapOf("email" to email)
//            }
//        }
//
//        VolleyHelper.getInstance(context).addToRequestQueue(request)
//    }
}
