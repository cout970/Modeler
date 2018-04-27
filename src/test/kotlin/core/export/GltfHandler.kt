package core.export

import com.cout970.modeler.core.export.ModelImporters.glftImporter
import com.cout970.modeler.util.toResourcePath
import org.junit.Test
import java.io.File

/**
 * Created by cout970 on 2017/06/06.
 */

class GltfHandler {

    @Test
    fun `Try importing a cube mode`() {
        println(File(".").absolutePath)
        val path = File("run/box.gltf").toResourcePath()

        val model = glftImporter.import(path)

        print(model)
    }
}