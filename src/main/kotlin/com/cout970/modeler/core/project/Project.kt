package com.cout970.modeler.core.project

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.model.Model
import com.google.gson.annotations.Expose
import java.io.Serializable
import java.util.*

/**
 * Created by cout970 on 2017/01/04.
 */
class Project(
        @Expose val owner: Author,
        @Expose var name: String
) : Serializable {
    @Expose var description: String = ""
    @Expose var creationTime: Long = Calendar.getInstance().timeInMillis

    @Expose var model: IModel = Model()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Project) return false

        if (owner != other.owner) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (creationTime != other.creationTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = owner.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + creationTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "Project(owner=$owner, name='$name', description='$description', creationTime=$creationTime)"
    }


}