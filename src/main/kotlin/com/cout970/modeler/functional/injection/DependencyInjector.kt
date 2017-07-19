package com.cout970.modeler.functional.injection

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.ActionExecutor
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.functional.FutureExecutor
import com.cout970.modeler.functional.TaskHistory
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.window.Loop
import com.cout970.modeler.view.window.WindowHandler
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/19.
 */

class DependencyInjector {

    fun injectDependencies(state: ProgramState, comp: Component?, obj: Any) {
        val clazz = obj::class.java
        val properties = clazz.declaredFields
                .filter { it.isAnnotationPresent(Inject::class.java) }
                .map { it.genericType to it }

        val valueAndProperty = properties.map { (type, property) ->
            val value: Any? = state.run {
                when (type) {
                    ISelection::class.java -> gui.selectionHandler.getSelection()
                    IModel::class.java -> projectManager.model
                    ProjectProperties::class.java -> projectManager.projectProperties

                    ResourceLoader::class.java -> resourceLoader
                    EventController::class.java -> eventController
                    WindowHandler::class.java -> windowHandler
                    RenderManager::class.java -> renderManager
                    Gui::class.java -> gui
                    ProjectManager::class.java -> projectManager
                    ActionExecutor::class.java -> actionExecutor
                    Loop::class.java -> mainLoop
                    ExportManager::class.java -> exportManager
                    FutureExecutor::class.java -> futureExecutor
                    TaskHistory::class.java -> taskHistory
                    else -> null
                }
            }
            value to property
        }

        valueAndProperty.forEach { (value, property) ->
            if (value == null) {
                log(Level.ERROR) { "Error finding a value for property: $property in $clazz" }
            } else {
                property.isAccessible = true
                property.set(obj, value)
            }
        }
    }
}