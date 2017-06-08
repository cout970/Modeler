package core.export

import com.cout970.modeler.core.export.TblImporter
import com.cout970.modeler.core.resource.toResourcePath
import org.junit.Test
import java.io.File

/**
 * Created by cout970 on 2017/06/08.
 */
class TblHandler {

    val tblImporter = TblImporter()

    @Test
    fun `Try parse an gear model`() {
        val path = File("src/test/resources/model/gear.tbl").toResourcePath()

        val model = tblImporter.parse(path)

        println(model)
    }

    @Test
    fun `Try import an gear model`() {
        val path = File("src/test/resources/model/gear.tbl").toResourcePath()

        val model = tblImporter.import(path)

        println(model)
    }
}