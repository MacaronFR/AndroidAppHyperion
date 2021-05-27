package fr.macaron_dev.hyperion

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*

class HomeActivity: AppCompatActivity(), DisconnectDialog.DisconnectDialogListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.homeToolBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val navController = findNavController(R.id.navViewFragmentManager)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.frag_home), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val gcCount: ActionMenuItemView = findViewById(R.id.gcCount)
        CoroutineScope(Dispatchers.Default).launch {
            val profile = api.getProfile()
            withContext(Dispatchers.Main){
                gcCount.text = getString(R.string.gccount, profile.getInt("gc"))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onBackPressed() {
        val dial = DisconnectDialog();
        dial.show(supportFragmentManager, "Disconnect")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        finish()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navViewFragmentManager)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}