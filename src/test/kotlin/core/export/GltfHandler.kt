package core.export

import com.cout970.matrix.extensions.times
import com.cout970.modeler.core.export.ModelImporters.gltfImporter
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toJOML
import com.cout970.vector.extensions.vec3Of
import core.utils.assertEquals
import core.utils.getPath
import org.joml.Matrix4d
import org.junit.Test

/**
 * Created by cout970 on 2017/06/06.
 */

class GltfHandler {

    @Test
    fun `Try importing a cube mode`() {
        val path = getPath("model/box.gltf")

        val model = gltfImporter.import(path)

        println(model)
    }

    /**
     * The GLTF standard requires the order of application of transformation matrices to be `T * R * S`
     */
    @Test
    fun `Check transformation order`() {
        val translate = Matrix4d().apply { translate(1.0, 2.0, 3.0) }.toIMatrix()
        val rotate = Matrix4d().apply { rotate(quatOfAngles(vec3Of(10, 20, 30)).toJOML()) }.toIMatrix()
        val scale = Matrix4d().apply { scale(2.0, 3.0, 4.0) }.toIMatrix()

        val trs = TRSTransformation(
                translation = vec3Of(1, 2, 3),
                rotation = quatOfAngles(vec3Of(10, 20, 30)),
                scale = vec3Of(2, 3, 4)
        )

        assertEquals(
                trs.matrix,
                translate * rotate * scale
        )
    }
}