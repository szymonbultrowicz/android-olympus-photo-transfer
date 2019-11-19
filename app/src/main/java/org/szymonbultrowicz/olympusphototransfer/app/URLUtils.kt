package org.szymonbultrowicz.olympusphototransfer.app

import org.szymonbultrowicz.olympusphototransfer.extensions.contains

fun ensureProtocol(url: String): String {
    return when (url) {
        in Regex("^https?://.*") -> url
        else -> "http://$url"
    }
}