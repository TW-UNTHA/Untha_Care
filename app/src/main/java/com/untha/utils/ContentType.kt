package com.untha.utils

enum class ContentType(val description: String) {
    CATEGORY("category"),
    ROUTE("route"),
    AUDIO("audio"),
    LINK("link"),
//    PHONE("phone"),
//    AUDIO_START("audio_start"),
//    AUDIO_STOP("audio_stop")
}

enum class FirebaseEvent(val description: String) {
    AUDIO("select_audio"),
    ROUTE("start_route"),
    LINK("select_link"),
    CLOSE("close_button")
}
