package data

import com.dineshworkspace.juicevendor.BuildKonfig

object Config {
    val PROJECT_ID = BuildKonfig.PROJECT_ID.trim('\"')
    val APP_ID = BuildKonfig.APP_ID.trim('\"')
    val API_KEY = BuildKonfig.API_KEY.trim('\"')
    val FIREBASE_DB_URL = BuildKonfig.FIREBASE_DB_URL.trim('\"')
    val SERVICE_ACCOUNT_ID = BuildKonfig.SERVICE_ACCOUNT_ID.trim('\"')
    val PRINT_HTTP_LOGS = BuildKonfig.PRINT_HTTP_LOGS.trim('\"').toBoolean()
    val BASE_LOCATION = BuildKonfig.BASE_LOCATION.trim('\"')
    const val ORDERS_COLLECTION = "Orders"
    const val JUICES_COLLECTION = "Juices"
    const val USERS_COLLECTION = "Users"
    const val DATE_FORMAT = "dd-MM-yy"
}