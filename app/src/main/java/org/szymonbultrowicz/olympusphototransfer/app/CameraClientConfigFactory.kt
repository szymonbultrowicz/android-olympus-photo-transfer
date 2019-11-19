package org.szymonbultrowicz.olympusphototransfer.app

import android.content.SharedPreferences
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClientConfig
import java.net.URL

class CameraClientConfigFactory {
    companion object {
        fun fromPreferences(prefs: SharedPreferences): CameraClientConfig {
            val url = URL(
                ensureProtocol(
                    prefs.getString("connection_address", "http://192.168.0.10/DCIM")!!
                )
            )
            return CameraClientConfig(
                url.protocol,
                url.host,
                url.port,
                url.path
            )
        }
    }
}