package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.core.model.ObjectCubeNone
import com.cout970.modeler.core.model.pos
import com.cout970.modeler.core.model.size
import com.cout970.modeler.gui.editor.EditorPanel
import com.cout970.modeler.util.quatOfAngles
import com.cout970.modeler.util.toAxisRotations
import com.cout970.modeler.util.toNullable
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import org.funktionale.option.Option
import org.liquidengine.legui.component.Component
import javax.script.ScriptEngineManager

/**
 * Created by cout970 on 2017/07/20.
 */

class UpdateTemplateCube : IUseCase {

    override val key: String = "update.template.cube"

    companion object {
        val scriptEngine = ScriptEngineManager().getEngineByName("JavaScript")!!
    }

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Option<ISelection>
    @Inject lateinit var editorPanel: EditorPanel

    data class PipeArgs(
            val ref: IObjectRef,
            val offset: Float,
            val cmd: String,
            val txt: String,
            val cube: IObjectCube
    )

    override fun createTask(): ITask {
        return selection
                .toNullable()
                .flatMap {
                    val ref = component.metadata["cube_ref"] as? IObjectRef ?: return@flatMap null
                    val offset = component.metadata["offset"] as? Float ?: return@flatMap null
                    val cmd = component.metadata["command"] as? String ?: return@flatMap null
                    val text = component.metadata["content"] as? String ?: return@flatMap null
                    PipeArgs(ref, offset, cmd, text, ObjectCubeNone)
                }
                .flatMap { (ref, offset, cmd, text, _) ->
                    val cube = model.getObject(ref) as? IObjectCube ?: return@flatMap null
                    PipeArgs(ref, offset, cmd, text, cube)
                }
                .flatMap { (ref, offset, cmd, text, cube) ->
                    val newObject = updateCube(cube, cmd, text, offset) ?: return@flatMap null
                    Pair(ref, newObject)
                }
                .map { (ref, cube) ->
                    val newModel = model.modifyObjects(listOf(ref)) { _, _ -> cube }
                    TaskUpdateModel(model, newModel) as ITask
                }
                .getOr(TaskNone)
    }

    fun updateCube(cube: IObjectCube, cmd: String, input: String, offset: Float): IObjectCube? {
        val obj: IObjectCube = when (cmd) {
        //@formatter:off
            "cube.size.x" -> setSizeX(cube, x = getValue(input, cube.size.xf) + offset)
            "cube.size.y" -> setSizeY(cube, y = getValue(input, cube.size.yf) + offset)
            "cube.size.z" -> setSizeZ(cube, z = getValue(input, cube.size.zf) + offset)

            "cube.pos.x" -> setPosX(cube, x = getValue(input, cube.pos.xf) + offset)
            "cube.pos.y" -> setPosY(cube, y = getValue(input, cube.pos.yf) + offset)
            "cube.pos.z" -> setPosZ(cube, z = getValue(input, cube.pos.zf) + offset)

            "cube.rot.x" -> setRotationX(cube, x = getValue(input, cube.transformation.rotation.toAxisRotations().xf) + offset * 15f)
            "cube.rot.y" -> setRotationY(cube, y = getValue(input, cube.transformation.rotation.toAxisRotations().yf) + offset * 15f)
            "cube.rot.z" -> setRotationZ(cube, z = getValue(input, cube.transformation.rotation.toAxisRotations().zf) + offset * 15f)

            "cube.tex.x" -> setTextureOffsetX(cube, x = getValue(input, cube.textureOffset.xf) + offset)
            "cube.tex.y" -> setTextureOffsetY(cube, y = getValue(input, cube.textureOffset.yf) + offset)
            "cube.tex.scale" -> setTextureSize(cube, getValue(input, cube.textureSize.xf) + offset)
            else -> cube
        //@formatter:on
        }
        if (cube.size == obj.size &&
            cube.pos == obj.pos &&
            cube.transformation == obj.transformation &&
            cube.textureOffset == obj.textureOffset &&
            cube.textureSize == obj.textureSize) {

            return null
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
        val oldRot = cube.transformation.rotation.toAxisRotations()
        val trans = cube.transformation.copy(rotation = quatOfAngles(x.clampRot(), oldRot.y, oldRot.z))
        return cube.withTransformation(trans)
    }

    fun setRotationY(cube: IObjectCube, y: Float): IObjectCube {
        val oldRot = cube.transformation.rotation.toAxisRotations()

        val trans = cube.transformation.copy(rotation = quatOfAngles(oldRot.x, y.clampRot(), oldRot.z))
        return cube.withTransformation(trans)
    }

    fun setRotationZ(cube: IObjectCube, z: Float): IObjectCube {
        val oldRot = cube.transformation.rotation.toAxisRotations()
        val trans = cube.transformation.copy(rotation = quatOfAngles(oldRot.x, oldRot.y, z.clampRot()))
        return cube.withTransformation(trans)
    }

    fun setTextureOffsetX(cube: IObjectCube, x: Float): IObjectCube {
        return cube.withTextureOffset(vec2Of(x, cube.textureOffset.yf))
    }

    fun setTextureOffsetY(cube: IObjectCube, y: Float): IObjectCube {
        return cube.withTextureOffset(vec2Of(cube.textureOffset.xf, y))
    }

    fun setTextureSize(cube: IObjectCube, s: Float): IObjectCube {
        return cube.withTextureSize(vec2Of(s))
    }

    private fun Float.clampRot(): Double {
        return when {
            this > 180f -> this - 360f
            this < -180f -> this + 360f
            else -> this
        }.toDouble()
    }

    fun getValue(input: String, default: Float): Float {
        return try {
            (scriptEngine.eval(input) as? Number)?.toFloat() ?: default
        } catch (e: Exception) {
            default
        }
    }
}