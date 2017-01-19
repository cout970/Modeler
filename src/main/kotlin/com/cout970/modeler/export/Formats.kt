package com.cout970.modeler.export

/**
 * Created by cout970 on 2017/01/02.
 */
enum class ImportFormat {
    OBJ, //wavefront models
    TCN, //techne models
    JSON //minecraft models
}

enum class ExportFormat {
    OBJ, //wavefront models
}

data class ImportProperties(val path: String, val format: ImportFormat, val flipUV: Boolean)