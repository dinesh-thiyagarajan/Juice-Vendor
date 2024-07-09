package network

import com.dineshworkspace.juicevendor.BuildKonfig

object ApiConstants {
    val API_KEY = BuildKonfig.FIREBASE_API_KEY.trim('\"')
    val DATABASE_URL = BuildKonfig.FIREBASE_DATABASE_URL.trim('\"')
}