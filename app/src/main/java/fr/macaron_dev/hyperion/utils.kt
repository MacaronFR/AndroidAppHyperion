package fr.macaron_dev.hyperion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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