package fr.macaron_dev.hyperion.activity

import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.api
import fr.macaron_dev.hyperion.b64ToBitmap
import fr.macaron_dev.hyperion.data.Project
import fr.macaron_dev.hyperion.database.Hyperion
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.InvalidParameterException

class ProjectDetailActivity: AppCompatActivity() {

    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_detail_activity)
        project = intent.getSerializableExtra("project") as Project
        val db = HyperionDbHelper(applicationContext).readableDatabase
        val logo = findViewById<ImageView>(R.id.ProjectDetailLogo)
        val textName = findViewById<TextView>(R.id.ProjectDetailName)
        val textDesc = findViewById<TextView>(R.id.ProjectDetailDesc)
        val textLeft = findViewById<TextView>(R.id.ProjectDetailLeft)
        val textContrib = findViewById<TextView>(R.id.ProjectDetailContribution)
        val contributeButton = findViewById<Button>(R.id.button_contribute)
        contributeButton.setOnClickListener(contributeListener)

        val projection = arrayOf(Hyperion.Logo.COLUMN_NAME_CONTENT, BaseColumns._ID)
        val selection = "${Hyperion.Logo.COLUMN_NAME_ID} = ?"
        val selectionArgs = arrayOf(project.logo.toString())

        val cursor = db.query(
            Hyperion.Logo.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if(cursor.count > 0){
            cursor.moveToNext()
            try {
                val b64 = cursor.getString(cursor.getColumnIndexOrThrow(Hyperion.Logo.COLUMN_NAME_CONTENT))
                logo.setImageBitmap(b64ToBitmap(b64))
            }catch (e: InvalidParameterException){}
        }

        cursor.close()

        with(project) {
            textName.text = name
            textDesc.text = description
            textLeft.text = getString(R.string.days_left, left)
            textContrib.text = getString(R.string.contrib, contribution)
        }
    }

    private val contributeListener = View.OnClickListener {
        CoroutineScope(Dispatchers.Default).launch{
            if(api.postContribute(project.id, 1)){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "OK", Toast.LENGTH_SHORT).show()
                }
            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "NIK", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}