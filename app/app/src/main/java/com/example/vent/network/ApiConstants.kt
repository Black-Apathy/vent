package com.example.vent.network

object ApiConstants {
    // Base URL for the API
    private const val BASE_URL = "https://ammrsf-ip-122-170-2-205.tunnelmole.net"

    // Endpoints
    const val LOGIN_URL = "${BASE_URL}/register"
//    const val SIGNUP_URL = "${BASE_URL}auth/signup"
//    const val EVENT_REGISTER_URL = "${BASE_URL}events/register"
//    const val FETCH_EVENTS_URL = "${BASE_URL}events/list"

    // Timeout values
    const val TIMEOUT_CONNECT = 15_000 // 15 seconds
    const val TIMEOUT_READ = 15_000
}
