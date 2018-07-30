package core.export

import com.cout970.modeler.core.export.GlTFExporter
import com.cout970.modeler.core.export.ModelImporters.gltfImporter
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toJOML
import com.cout970.modeler.util.toResourcePath
import com.cout970.vector.extensions.vec3Of
import core.utils.assertEquals
import org.joml.Matrix4d
import org.junit.Test
import java.io.File

/**
 * Created by cout970 on 2017/06/06.
 */

class GltfHandler {

    @Test
    fun `Try importing a cube mode`() {
        val path = File("src/test/resources/model/box.gltf").toResourcePath()

        val model = gltfImporter.import(path)

        println(model)
    }

    /**
     * The program apply the transformations of TRS in the order `S * R * T` as matrix multiplication so
     * they are applied in the order: translate, then rotate and finally scale.
     * The GLTF standard requires the order of application to be `T * R * S` so this transformation is needed
     */
    @Test
    fun `Invert transformation order`() {
        val trs = TRSTransformation(
                translation = vec3Of(1, 2, 3),
                rotation = quatOfAngles(vec3Of(10, 20, 30)),
                scale = vec3Of(2, 3, 4)
        )

        val expected = Matrix4d().apply {
            scale(2.0, 3.0, 4.0)
            rotate(quatOfAngles(vec3Of(10, 20, 30)).toJOML())
            translate(1.0, 2.0, 3.0)
        }.toIMatrix()


        assertEquals(
                TRSTransformation.fromMatrix(expected),
                GlTFExporter.reorder(trs)
        )
    }
}