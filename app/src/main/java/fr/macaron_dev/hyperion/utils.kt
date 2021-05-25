package fr.macaron_dev.hyperion

import java.security.MessageDigest

val api = API();

fun hashSHA256(input: String): String{
    val digest = MessageDigest.getInstance("SHA-256")
    return  bytesToHex(digest.digest(input.encodeToByteArray()))
}

fun bytesToHex(hash: ByteArray): String {
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