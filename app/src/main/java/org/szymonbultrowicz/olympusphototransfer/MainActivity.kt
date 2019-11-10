package org.szymonbultrowicz.olympusphototransfer

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.szymonbultrowicz.olympusphototransfer.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.client.CameraClientConfig
import org.szymonbultrowicz.olympusphototransfer.sync.FilesManager
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    val LOG = Logger.getLogger(javaClass.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val camera = CameraClient(CameraClientConfig(
            "http",
            "192.168.1.102",
            8000,
            "/DCIM",
            "wlan.*=.*,(.*),(\\d+),(\\d+),(\\d+),(\\d+).*",
            true
        ))

        CoroutineScope(Dispatchers.Main).launch {
            val isConnected = checkIsConnected(camera)
            Logger.getLogger(this.javaClass.name).info("connected: $isConnected")
            if (isConnected) {
                syncFiles(camera)
            }
        }
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

    private suspend fun checkIsConnected(camera: CameraClient): Boolean = withContext(Dispatchers.IO) {
        camera.isConnected()
    }

    private suspend fun syncFiles(camera: CameraClient) = withContext(Dispatchers.IO) {
        val dir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dir != null) {
            val filesManager = FilesManager(
                camera,
                FilesManager.FilesManager.Config(dir)
            )

            filesManager.listRemoteFiles().forEach { LOG.info(it.name) }
        }

    }
}
