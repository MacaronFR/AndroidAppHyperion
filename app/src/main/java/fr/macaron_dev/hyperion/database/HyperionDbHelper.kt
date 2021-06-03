package fr.macaron_dev.hyperion.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class HyperionDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        const val DATABASE_NAME = "HYPERION"
        const val DATABASE_VERSION = 1
    }

    private val CREATE_ENTRIES = "CREATE TABLE ${Hyperion.Logo.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${Hyperion.Logo.COLUMN_NAME_CONTENT} TEXT," +
            "${Hyperion.Logo.COLUMN_NAME_ID} INTEGER)"

    private val DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Hyperion.Logo.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_ENTRIES)
        onCreate(db)
    }
}