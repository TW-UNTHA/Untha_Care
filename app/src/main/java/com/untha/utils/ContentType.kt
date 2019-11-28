package com.untha.utils

enum class ContentType(val description: String) {
    CATEGORY("category"),
    ROUTE("route"),
    AUDIO("audio"),
    LINK("link"),
    CLOSE("close"),
    HELP("help"),
    SHARE("share"),

//    PHONE("phone"),
//    AUDIO_START("audio_start"),
//    AUDIO_STOP("audio_stop")
}

enum class FirebaseEvent(val description: String) {
    AUDIO("select_audio"),
    ROUTE("start_route"),
    LINK("select_link"),
    CLOSE("close_action_bar"),
    HELP("help_action_bar"),
    SHARE("share"),

}
