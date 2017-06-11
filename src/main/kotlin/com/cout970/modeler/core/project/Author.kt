package com.cout970.modeler.core.project

/**
 * Created by cout970 on 2017/01/04.
 */
class Author(var name: String = "Anonymous", var email: String = "") {

    var web: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Author) return false

        if (name != other.name) return false
        if (email != other.email) return false
        if (web != other.web) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (web?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Author(name='$name', email='$email', web=$web)"
    }
}