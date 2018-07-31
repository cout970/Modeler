package core.export

import com.cout970.modeler.core.export.ModelImporters.objImporter
import com.cout970.modeler.util.toResourcePath
import core.utils.getPath
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Test
import java.io.File

/**
 * Created by cout970 on 2017/06/04.
 */
class ObjHandler {

    @Test
    fun `Try to import a cube model`() {
        val path = getPath("model/cube.obj")

        val mesh = objImporter.importAsMesh(path, false)

        assertEquals("Invalid number of position vertex", 8, mesh.pos.size)
        assertEquals("Invalid number of texture vertex", 1, mesh.tex.size)
        assertEquals("Invalid number of faces", 6, mesh.faces.size)
    }

    @Test
    fun `Try to import a cube model but flipping the UV`() {
        val path = getPath("model/cube.obj")

        val mesh = objImporter.importAsMesh(path, true)

        assertEquals("Invalid number of position vertex", 8, mesh.pos.size)
        assertEquals("Invalid number of texture vertex", 1, mesh.tex.size)
        assertEquals("Invalid number of faces", 6, mesh.faces.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Try to import a model with and invalid path`() {
        val path = File("").toResourcePath()

        objImporter.importAsMesh(path, false)

        fail("No exception thrown")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Try to import a model with and random path`() {
        val path = File("#@$@#%^!@#@$").toResourcePath()

        objImporter.importAsMesh(path, false)

        fail("No exception thrown")
    }
}