package com.cout970.modeler.core.export

import com.cout970.modeler.PathConstants
import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.export.project.ProjectLoaderV10
import com.cout970.modeler.core.export.project.ProjectLoaderV11
import com.cout970.modeler.core.export.project.ProjectLoaderV12
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.event.Notification
import com.cout970.modeler.gui.event.NotificationHandler
import com.cout970.modeler.util.createParentsIfNeeded
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*
import java.util.zip.ZipFile

/**
 * Created by cout970 on 2017/01/02.
 */
class ExportManager(val resourceLoader: ResourceLoader) {

    companion object {
        const val CURRENT_SAVE_VERSION = "1.2"
        val VERSION_GSON = GsonBuilder().create()!!
    }

    fun loadProject(path: String): ProgramSave {
        val zip = ZipFile(path)

        val version = zip.load<String>("version.json", VERSION_GSON)
                ?: throw IllegalStateException("Missing file 'version.json' inside '$path'")

        return when (version) {
            "1.0" -> ProjectLoaderV10.loadProject(zip, path)
            "1.1" -> ProjectLoaderV11.loadProject(zip, path)
            "1.2" -> ProjectLoaderV12.loadProject(zip, path)
            else -> throw IllegalStateException("Invalid save version $version")
        }
    }

    fun saveProject(path: String, save: ProgramSave) {
        log(Level.FINE) { "Starting project save" }
        File(path).createParentsIfNeeded()
        ProjectLoaderV12.saveProject(path, save)
        log(Level.FINE) { "Project saved" }
    }

    fun saveProject(path: String, manager: ProjectManager, saveImages: Boolean) {
        saveProject(path, ProgramSave(CURRENT_SAVE_VERSION, manager.projectProperties, manager.model,
                manager.animation, if (saveImages) manager.materialPaths else emptyList()))
    }

    fun import(file: String): IModel {
        return loadProject(file).model.modifyObjects({ true }) { _, obj -> obj.withId(UUID.randomUUID()) }
    }

    fun loadLastProjectIfExists(projectManager: ProjectManager, gui: Gui) {
        val path = File(PathConstants.LAST_BACKUP_FILE_PATH)
        if (path.exists()) {
            try {
                log(Level.FINE) { "Found last project, loading..." }
                val save = loadProject(path.path)

                projectManager.loadProjectProperties(save.projectProperties)
                projectManager.updateModel(save.model)
                projectManager.updateAnimation(save.animation)
                gui.windowHandler.updateTitle(save.projectProperties.name)

                save.model.materials.forEach { it.loadTexture(resourceLoader) }

                log(Level.FINE) { "Last project loaded" }
                NotificationHandler.push(Notification("Project loaded",
                        "Loaded project from last execution"))
            } catch (e: Exception) {
                log(Level.ERROR) { "Unable to load last project" }
                e.print()
                NotificationHandler.push(Notification("Error loading project",
                        "Unable to load project at '${path.absolutePath}': $e"))
            }
        } else {
            log(Level.FINE) { "No last project found, ignoring" }
        }
    }
}

data class ProgramSave(
        val version: String,
        val projectProperties: ProjectProperties,
        val model: IModel,
        val animation: IAnimation,
        val textures: List<TexturedMaterial>
)