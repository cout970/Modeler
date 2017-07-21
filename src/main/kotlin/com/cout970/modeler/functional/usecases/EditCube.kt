package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskNone
import com.cout970.modeler.functional.tasks.TaskUpdateModel
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.gui.comp.CTextInput
import com.cout970.modeler.view.gui.comp.Cache
import com.cout970.modeler.view.gui.editor.EditorPanel
import com.cout970.vector.extensions.*
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/07/20.
 */

class UpdateTemplateCube : IUseCase {

    override val key: String = "update.template.cube"

    val scriptEngine get() = CTextInput.scriptEngine

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel
    @Inject var selection: ISelection? = null
    @Inject lateinit var editorPanel: EditorPanel

    override fun createTask(): ITask {

        selection?.let { selection ->
            val presenter = editorPanel.leftPanelModule.presenter
            val element = presenter.getSelectedCube(model, selection)
            val ref = presenter.getSelectedCubeRef(model, selection)

            element?.let { cube ->
                ref!!
                val comp = component
                val (offset: Float, input: CTextInput) = when (comp) {
                    is Cache -> comp.cache["offset"] as Float to comp.subComponents.first() as CTextInput
                    is CTextInput -> 0f to comp
                    else -> return TaskNone
                }
                val newObject = updateCube(cube, input, offset)
                newObject?.let {
                    val newModel = model.modifyObjects(listOf(ref)) { _, _ -> newObject }
                    return TaskUpdateModel(model, newModel)
                }
            }
        }
        return TaskNone
    }

    fun updateCube(cube: IObjectCube, input: CTextInput, offset: Float): IObjectCube? {
        val obj: IObjectCube? = when (input.id) {
        //@formatter:off
            "cube.size.x" -> setSizeX(cube, x = getValue(input, cube.size.xf) + offset)
            "cube.size.y" -> setSizeY(cube, y = getValue(input, cube.size.yf) + offset)
            "cube.size.z" -> setSizeZ(cube, z = getValue(input, cube.size.zf) + offset)

            "cube.pos.x" -> setPosX(cube, x = getValue(input, cube.pos.xf) + offset)
            "cube.pos.y" -> setPosY(cube, y = getValue(input, cube.pos.yf) + offset)
            "cube.pos.z" -> setPosZ(cube, z = getValue(input, cube.pos.zf) + offset)

            "cube.rot.x" -> setRotationX(cube, x = getValue(input, cube.subTransformation.rotation.toDegrees().xf) + offset)
            "cube.rot.y" -> setRotationY(cube, y = getValue(input, cube.subTransformation.rotation.toDegrees().yf) + offset)
            "cube.rot.z" -> setRotationZ(cube, z = getValue(input, cube.subTransformation.rotation.toDegrees().zf) + offset)
            else -> null
            //@formatter:on
        }
        if (obj != null) {
            if (cube.size == obj.size && cube.pos == obj.pos && cube.subTransformation == obj.subTransformation) {
                return null
            }
        }
        return obj
    }

    fun setSizeX(cube: IObjectCube, x: Float): IObjectCube {
        return cube.withSize(vec3Of(x, cube.size.y, cube.size.z))
    }

    fun setSizeY(cube: IObjectCube, y: Float): IObjectCube {
        return cube.withSize(vec3Of(cube.size.x, y, cube.size.z))
    }

    fun setSizeZ(cube: IObjectCube, z: Float): IObjectCube {
        return cube.withSize(vec3Of(cube.size.x, cube.size.y, z))
    }

    fun setPosX(cube: IObjectCube, x: Float): IObjectCube {
        return cube.withPos(vec3Of(x, cube.pos.y, cube.pos.z))
    }

    fun setPosY(cube: IObjectCube, y: Float): IObjectCube {
        return cube.withPos(vec3Of(cube.pos.x, y, cube.pos.z))
    }

    fun setPosZ(cube: IObjectCube, z: Float): IObjectCube {
        return cube.withPos(vec3Of(cube.pos.x, cube.pos.y, z))
    }

    fun setRotationX(cube: IObjectCube, x: Float): IObjectCube {
        val oldRot = cube.subTransformation.rotation
        val trans = cube.subTransformation.copy(rotation = vec3Of(x.clampRot(), oldRot.y, oldRot.z))
        return cube.withSubTransformation(trans)
    }

    fun setRotationY(cube: IObjectCube, y: Float): IObjectCube {
        val oldRot = cube.subTransformation.rotation
        val trans = cube.subTransformation.copy(rotation = vec3Of(oldRot.x, y.clampRot(), oldRot.z))
        return cube.withSubTransformation(trans)
    }

    fun setRotationZ(cube: IObjectCube, z: Float): IObjectCube {
        val oldRot = cube.subTransformation.rotation
        val trans = cube.subTransformation.copy(rotation = vec3Of(oldRot.x, oldRot.y, z.clampRot()))
        return cube.withSubTransformation(trans)
    }

    private fun Float.clampRot(): Double {
        return when {
            this > 180f -> this - 360f
            this < -180f -> this + 360f
            else -> this
        }.toRads()
    }

    fun getValue(input: TextInput, default: Float): Float {
        try {
            return (scriptEngine.eval(input.text) as? Number)?.toFloat() ?: default
        } catch (e: Exception) {
            return default
        }
    }
}