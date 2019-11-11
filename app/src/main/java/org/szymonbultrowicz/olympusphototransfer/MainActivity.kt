package org.szymonbultrowicz.olympusphototransfer

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.szymonbultrowicz.olympusphototransfer.app.CameraClientConfigFactory
import org.szymonbultrowicz.olympusphototransfer.app.SettingsActivity
import org.szymonbultrowicz.olympusphototransfer.app.photolist.PhotoListFragment
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
import java.io.File
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

    override fun onListFragmentInteraction(item: FileInfo?) {
        if (item == null) {
            return
        }
        Toast.makeText(applicationContext, "Started downloading the file", Toast.LENGTH_SHORT)
            .show()

        val camera = CameraClient(CameraClientConfigFactory.fromPreferences(
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        ))
        val targetDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (targetDir == null) {
            Toast.makeText(applicationContext, "Failed to mount target directory", Toast.LENGTH_SHORT)
                .show()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            val f = downloadFile(item, camera, targetDir)
            val text = if (f != null) "Photo downloaded" else "Failed to download photo"
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private suspend fun downloadFile(
        file: FileInfo,
        camera: CameraClient,
        targetDir: File
    ) = withContext(Dispatchers.IO) {
        camera.downloadFile(file, targetDir)
    }
}
