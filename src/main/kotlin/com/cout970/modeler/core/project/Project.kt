package com.cout970.modeler.core.project

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.ModelResources
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

    @Expose var model: Model = Model(listOf(), ModelResources())
}