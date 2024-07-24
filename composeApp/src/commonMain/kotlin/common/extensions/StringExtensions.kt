package common.extensions

fun String.removeSpacesAndLowerCase(): String {
    return this.run {
        replace(" ", "").lowercase()
    }
}

