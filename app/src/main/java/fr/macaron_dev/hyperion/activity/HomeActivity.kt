package fr.macaron_dev.hyperion.activity

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import fr.macaron_dev.hyperion.ui.dialog.DisconnectDialog
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.api
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.*

class HomeActivity: AppCompatActivity(), DisconnectDialog.DisconnectDialogListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.homeToolBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val navController = findNavController(R.id.navViewFragmentManager)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_project, R.id.nav_about, R.id.nav_admin, R.id.nav_add), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        val gcCount: ActionMenuItemView = findViewById(R.id.gcCount)
        CoroutineScope(Dispatchers.Default).launch {
            val profile = api.getProfile()
            if(profile.has("status")){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_LONG)
                }
            }
            withContext(Dispatchers.Main){
                gcCount.text = getString(R.string.gccount, profile.getInt("gc"))
                findViewById<TextView>(R.id.name).text = getString(R.string.name, profile.getString("name"), profile.getString("fname"))
                findViewById<TextView>(R.id.mail).text = getString(R.string.mail, profile.getString("mail"))
                if(profile.getInt("type") >= 3){
                    navView.menu.removeGroup(R.id.group_nav_admin)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onBackPressed() {
        val dial = DisconnectDialog()
        dial.show(supportFragmentManager, "Disconnect")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        applicationContext.deleteDatabase(HyperionDbHelper.DATABASE_NAME)
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