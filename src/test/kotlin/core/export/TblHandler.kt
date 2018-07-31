package core.export

import com.cout970.modeler.core.export.ModelImporters.tblImporter
import core.utils.getPath
import org.junit.Assert
import org.junit.Test

/**
 * Created by cout970 on 2017/06/08.
 */
class TblHandler {

    @Test
    fun `Try parse an gear model`() {
        val path = getPath("model/gear.tbl")

        val model = tblImporter.import(path)

        Assert.assertEquals("Invalid number of cubes", 64, model.objectMap.size)
        Assert.assertEquals("Invalid number of groups", 0, model.groupMap.size)
    }
}