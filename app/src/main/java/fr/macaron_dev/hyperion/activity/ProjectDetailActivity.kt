package fr.macaron_dev.hyperion.activity

import android.os.Bundle
import android.provider.BaseColumns
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.b64ToBitmap
import fr.macaron_dev.hyperion.data.Project
import fr.macaron_dev.hyperion.database.Hyperion
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import java.security.InvalidParameterException

class ProjectDetailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_detail_activity)
        val project: Project = intent.getSerializableExtra("project") as Project
        val db = HyperionDbHelper(applicationContext).readableDatabase
        val logo = findViewById<ImageView>(R.id.ProjectDetailLogo)
        val textName = findViewById<TextView>(R.id.ProjectDetailName)
        val textDesc = findViewById<TextView>(R.id.ProjectDetailDesc)
        val textLeft = findViewById<TextView>(R.id.ProjectDetailLeft)
        val textContrib = findViewById<TextView>(R.id.ProjectDetailContribution)

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
}