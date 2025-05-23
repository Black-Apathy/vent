package com.example.vent.network

object ApiConstants {
    // Base URL for the API
    private const val BASE_URL = "https://qrfg2o-ip-116-72-105-219.tunnelmole.net/"

    // Endpoints
    const val LOGIN_URL = "${BASE_URL}register"
    const val FETCH_PENDING_USERS_URL = "${BASE_URL}pending-user"
    const val ACCEPT_USER_URL = "${BASE_URL}approve-user"
    const val REJECT_USER_URL = "${BASE_URL}reject-user"
    const val CHECK_STATUS_URL = "${BASE_URL}check-user-status"
//    const val SIGNUP_URL = "${BASE_URL}auth/signup"
//    const val EVENT_REGISTER_URL = "${BASE_URL}events/register"
//    const val FETCH_EVENTS_URL = "${BASE_URL}events/list"

    // Timeout values
    const val TIMEOUT_CONNECT = 15_000 // 15 seconds
    const val TIMEOUT_READ = 15_000
}
