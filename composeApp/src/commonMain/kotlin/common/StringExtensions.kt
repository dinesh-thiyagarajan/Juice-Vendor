package common

fun String.removeSpacesAndLowerCase(): String {
    return this.run {
        replace(" ", "").lowercase()
    }
}

