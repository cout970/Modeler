package com.cout970.modeler.core.project

import java.io.Serializable
import java.util.*

/**
 * Created by cout970 on 2017/01/04.
 */
data class Project(
        val owner: Author,
        val name: String,
        val description: String = "",
        val creationTime: Long = Calendar.getInstance().timeInMillis
) : Serializable