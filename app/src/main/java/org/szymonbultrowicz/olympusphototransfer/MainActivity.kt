package org.szymonbultrowicz.olympusphototransfer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.szymonbultrowicz.olympusphototransfer.app.SettingsActivity
import org.szymonbultrowicz.olympusphototransfer.app.photolist.PhotoListFragment
import org.szymonbultrowicz.olympusphototransfer.lib.client.FileInfo
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
