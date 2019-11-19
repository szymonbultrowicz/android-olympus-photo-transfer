package org.szymonbultrowicz.olympusphototransfer.app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import org.szymonbultrowicz.olympusphototransfer.R
import java.net.URI
import java.net.URISyntaxException

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            preferenceScreen.findPreference<EditTextPreference>("connection_address")
                ?.setOnPreferenceChangeListener { _, newValue ->
                    when(newValue) {
                        !is String -> false
                        null -> false
                        else -> try {
                                URI(ensureProtocol(newValue)).host != null
                            } catch (e: URISyntaxException) {
                                false
                            }
                    }
                }
        }
    }
}