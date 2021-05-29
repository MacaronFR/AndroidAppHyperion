package fr.macaron_dev.hyperion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        val mail = intent.getStringExtra("mail") as String
        val hash = intent.getStringExtra("hash") as String
        CoroutineScope(Dispatchers.Default).launch {
            when(api.connect(mail, hash)){
                0 -> withContext(Dispatchers.Main) {
                    finish()
                    val homeIntent = Intent(applicationContext, HomeActivity::class.java)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(homeIntent)
                }
                1 -> withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Connection to Server cannot be established", Toast.LENGTH_SHORT).show()
                    finish()
                }
                2 -> withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Mauvais Identifiant", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        return
    }
}