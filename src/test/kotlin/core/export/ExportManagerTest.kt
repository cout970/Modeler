package core.export

import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.model.Model
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.Project
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.core.resource.toResourcePath
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * Created by cout970 on 2017/06/09.
 */

class ExportManagerTest {

    val exportManager = ExportManager(ResourceLoader())

    @Test
    fun `Try save and load a project`() {
        val path = "./run/test_load_save.pff"
        val project = Project(Author("Me", "Me@Me.Me"), "Unnamed")

        exportManager.saveProject(path, project)

        val loadedProject = exportManager.loadProject(path)

        Assert.assertEquals(project, loadedProject)
    }


    @Test
    fun `Try load and obj and export it as a project`() {
        val path = File("src/test/resources/model/arrows.obj").toResourcePath()
        val savePath = "./run/test_obj_to_pff.pff"

        val mesh = exportManager.objImporter.importAsMesh(path, true)
        val project = Project(Author(), "")
        project.model = Model(listOf(Object("Shape 1", mesh)))

        exportManager.saveProject(savePath, project)
    }

    @Test
    fun `Convert tcn file to Obj`() {
        val path = File("src/test/resources/model/conveyor_belt.tcn").toResourcePath()
        val output = File("./run/output.obj")

        val model = exportManager.tcnImporter.import(path)
        exportManager.objExporter.export(output.outputStream(), model, "conveyor_belt")
    }
}