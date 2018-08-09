package com.cout970.modeler.core.export

/**
 * Created by cout970 on 2017/01/02.
 */
enum class ImportFormat {
    OBJ, //wavefront models
    TCN, //techne models
    JSON,//minecraft models
    TBL, //tabula
    MCX, // custom format for minecraft
    PFF,  // project file
    GLTF  // GL transport format
}

enum class ExportFormat {
    OBJ, //wavefront models
    MCX,
    GLTF
}

data class ImportProperties(
        val path: String,
        val format: ImportFormat,
        val flipUV: Boolean,
        val append: Boolean
)

sealed class ExportProperties(
        val path: String,
        val format: ExportFormat
)

class GltfExportProperties(
        path: String
) : ExportProperties(path, ExportFormat.GLTF)


class McxExportProperties(
        path: String,
        val domain: String
) : ExportProperties(path, ExportFormat.MCX)

class ObjExportProperties(
        path: String,
        val materialLib: String,
        val useNormals: Boolean,
        val flipUV: Boolean
) : ExportProperties(path, ExportFormat.OBJ)

data class ExportTextureProperties(
        val path: String,
        val size: Int
)