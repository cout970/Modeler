package com.cout970.modeler.core.export

/**
 * Created by cout970 on 2017/01/02.
 */
enum class ImportFormat {
    OBJ, //wavefront models
    TCN, //techne models
    JSON, //minecraft models
    TBL //tabula
}

enum class ExportFormat {
    OBJ, //wavefront models
    MCX
}

data class ImportProperties(val path: String, val format: ImportFormat, val flipUV: Boolean)
data class ExportProperties(val path: String, val format: ExportFormat, val materialLib: String = "materials",
                            val domain: String = "blacksmith")