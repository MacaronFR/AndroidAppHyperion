package fr.macaron_dev.hyperion

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import android.util.Base64
import fr.macaron_dev.hyperion.database.Hyperion
import java.security.InvalidParameterException
import java.security.MessageDigest

val api = API()

fun hashSHA256(input: String): String{
    val digest = MessageDigest.getInstance("SHA-256")
    return  bytesToHex(digest.digest(input.encodeToByteArray()))
}

private fun bytesToHex(hash: ByteArray): String{
    val hexString = StringBuilder(2 * hash.size)
    for (i in hash.indices) {
        val hex = Integer.toHexString(0xff and hash[i].toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}

fun b64ToBitmap(b64: String): Bitmap{
    val bytes = Base64.decode(b64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: throw InvalidParameterException("Error b64 string not image")
}

fun retrieveLogo(id: Int, db: SQLiteDatabase): String?{
    val projection = arrayOf(Hyperion.Logo.COLUMN_NAME_CONTENT, BaseColumns._ID)
    val selection = "${Hyperion.Logo.COLUMN_NAME_ID} = ?"
    val selectionArgs = arrayOf(id.toString())

    val cursor = db.query(
        Hyperion.Logo.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        null
    )
    return if(cursor.count > 0){
        cursor.moveToNext()
        val b64 = cursor.getString(cursor.getColumnIndex(Hyperion.Logo.COLUMN_NAME_CONTENT))
        cursor.close()
        b64
    }else{
        null
    }
}