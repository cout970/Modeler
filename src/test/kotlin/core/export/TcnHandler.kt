package core.export

import com.cout970.modeler.core.export.TcnImporter
import com.cout970.modeler.core.resource.toResourcePath
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * Created by cout970 on 2017/06/06.
 */

class TcnHandler {

    val tcnImporter = TcnImporter()

    @Test
    fun `Try importing a cube mode`() {
        val path = File("src/test/resources/model/cube.tcn").toResourcePath()

        val model = tcnImporter.import(path)

        Assert.assertEquals("Invalid number of cubes", 1, model.objects.size)
    }
}