package fr.macaron_dev.hyperion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login = findViewById<Button>(R.id.login)
        login.setOnClickListener(connect)
    }

    private val connect = View.OnClickListener {
        val mail = findViewById<EditText>(R.id.mail).text.toString()
        val password = findViewById<EditText>(R.id.password)
        val hash = hashSHA256(password.text.toString())
        val loadingIntent = Intent(applicationContext, LoadingActivity::class.java)
        loadingIntent.putExtra("mail", mail)
        loadingIntent.putExtra("hash", hash)
        startActivity(loadingIntent)
    }
}