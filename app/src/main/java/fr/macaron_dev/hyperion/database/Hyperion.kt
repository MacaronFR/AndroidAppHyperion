package fr.macaron_dev.hyperion.database

import android.provider.BaseColumns

object Hyperion {
    object Logo: BaseColumns{
        const val TABLE_NAME = "LOGO"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_CONTENT = "content"
    }
}