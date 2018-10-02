package com.cout970.modeler.core.export

/**
 * Created by cout970 on 2017/06/14.
 */
object ModelImporters {

    val objImporter = ObjImporter()
    val objExporter = ObjExporter()

    val vsImporter = VsImporter()
    val vsExporter = VsExporter()

    val gltfImporter = GlTFImporter()
    val gltfExporter = GlTFExporter()

    val tcnImporter = TcnImporter()
    val jsonImporter = JsonImporter()
    val tblImporter = TblImporter()

    val mcxExporter = McxExporter()
    val mcxImporter = McxImporter()
}