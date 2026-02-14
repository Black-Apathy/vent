package com.example.vent.network

object ApiConstants {
    // Base URL for the API
    private const val BASE_URL = "http://127.0.0.1:3000/"

    // Endpoints
    const val REGISTER_URL = "${BASE_URL}register"
    const val FETCH_PENDING_USERS_URL = "${BASE_URL}pending-user"
    const val FETCH_ALL_USERS_URL = "${BASE_URL}all-users"
    const val ACCEPT_USER_URL = "${BASE_URL}approve-user"
    const val REJECT_USER_URL = "${BASE_URL}reject-user"
    const val CHECK_STATUS_URL = "${BASE_URL}check-user-status"
    const val LOGIN_URL = "${BASE_URL}login"
    const val VIEW_EVENTS = "${BASE_URL}events"
    const val CREATE_EVENT_URL = "${BASE_URL}events"
    const val RESET_PASSWORD_URL = "${BASE_URL}reset-password"
    const val REFRESH_TOKEN_URL = "${BASE_URL}refresh-token"

    // Timeout values
    const val TIMEOUT_CONNECT = 15_000
    const val TIMEOUT_READ = 15_000

    // --- PDF Download Function ---
    // This takes the ID and returns the full URL: "https://.../events/123/pdf"
    fun getEventPdfUrl(eventId: String): String {
        return "${BASE_URL}events/$eventId/pdf"
    }
}