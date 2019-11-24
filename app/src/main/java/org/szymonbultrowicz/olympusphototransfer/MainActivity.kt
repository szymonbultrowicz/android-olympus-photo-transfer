package org.szymonbultrowicz.olympusphototransfer

import android.content.Intent
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.szymonbultrowicz.olympusphototransfer.app.CameraClientConfigFactory
import org.szymonbultrowicz.olympusphototransfer.app.PhotoFileDownloader
import org.szymonbultrowicz.olympusphototransfer.app.SettingsActivity
import org.szymonbultrowicz.olympusphototransfer.app.photolist.PhotoListFragment
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.PhotoInfo
import java.lang.Exception
import java.util.logging.Level
import java.util.logging.Logger

class MainActivity : AppCompatActivity(), PhotoListFragment.OnListFragmentInteractionListener {

    private val logger = Logger.getLogger(javaClass.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onListFragmentInteraction(item: PhotoInfo?) {
        if (item == null) {
            return
        }
        Toast.makeText(applicationContext, "Started downloading ${item.files.size} file(s) of ${item.name}", Toast.LENGTH_SHORT)
            .show()

        val camera = CameraClient(CameraClientConfigFactory.fromPreferences(
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        ))
        val downloader =
            PhotoFileDownloader()
        item.files.forEach { fileInfo ->
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    downloader.downloadFile(
                        fileInfo,
                        camera,
                        applicationContext.contentResolver
                    )
                    Toast.makeText(applicationContext, "File ${fileInfo.name} downloaded", Toast.LENGTH_SHORT)
                        .show()
                } catch(e: Exception) {
                    showError(e)
                }
            }
        }
    }

    private fun showError(e: Exception) {
        val text = "Failed to download photo"
        logger.log(Level.SEVERE, text, e)
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
            .show()
    }
}
