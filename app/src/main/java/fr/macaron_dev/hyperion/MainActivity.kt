package fr.macaron_dev.hyperion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
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
        val api = API()
        CoroutineScope(Dispatchers.Default).launch {
            if(api.connect(mail, hash)){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "OK", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Mauvais Identifiant", Toast.LENGTH_SHORT).show()
            }
        }
    }
}