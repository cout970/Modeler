package com.cout970.modeler.gui

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.fromClasspath
import org.liquidengine.legui.image.StbBackedLoadableImage

/**
 * Created by cout970 on 2017/06/14.
 */
class GuiResources {

    lateinit var baseCubeMesh: IMesh
    lateinit var orientationCubeMesh: IMesh
    lateinit var lightMesh: IMesh
    lateinit var translationArrow: IMesh
    lateinit var rotationRing: IMesh
    lateinit var scaleArrow: IMesh
    lateinit var skybox: IMesh

    lateinit var baseCubeTexture: Texture
    lateinit var orientationCube: Texture
    lateinit var centerMarkTexture: Texture
    lateinit var skyboxTexture: Texture

    val iconMap = mutableMapOf<String, StbBackedLoadableImage>()

    fun reload(loader: ResourceLoader) {
        log(Level.FINE) { "[GuiResources] Loading gui resources" }
        baseCubeMesh = ModelImporters.objImporter.importAsMesh("assets/models/cube.obj".fromClasspath(), true)
        orientationCubeMesh = ModelImporters.objImporter.importAsMesh("assets/models/orientation_cube.obj".fromClasspath(), false)
        lightMesh = ModelImporters.objImporter.importAsMesh("assets/models/light.obj".fromClasspath(), true)
        translationArrow = ModelImporters.objImporter.importAsMesh("assets/models/translation_x.obj".fromClasspath(),
                true)
        rotationRing = ModelImporters.objImporter.importAsMesh("assets/models/rotation_x.obj".fromClasspath(), true)
        scaleArrow = ModelImporters.objImporter.importAsMesh("assets/models/scale_x.obj".fromClasspath(), true)
        skybox = ModelImporters.objImporter.importAsMesh("assets/models/skybox.obj".fromClasspath(), false)

        baseCubeTexture = loader.getTexture("assets/textures/models/cube.png").apply { magFilter = Texture.PIXELATED }
        orientationCube =
            loader.getTexture("assets/textures/models/orientation_cube.png").apply { magFilter = Texture.PIXELATED }
        centerMarkTexture = loader.getTexture("assets/textures/models/center_mark.png")
        skyboxTexture = loader.getTextureCubeMap("assets/textures/models/skybox").apply {
            magFilter = Texture.SMOOTH
            minFilter = Texture.SMOOTH
            wrapT = Texture.CLAMP_TO_EDGE
            wrapS = Texture.CLAMP_TO_EDGE
            wrapW = Texture.CLAMP_TO_EDGE
        }

        iconMap["deleteIcon"] = loader.getImage("assets/textures/delete.png")
        iconMap["showIcon"] = loader.getImage("assets/textures/show.png")
        iconMap["hideIcon"] = loader.getImage("assets/textures/hide.png")

        iconMap["add_color_material"] = loader.getImage("assets/textures/add_colored_material.png")
        iconMap["config_material"] = loader.getImage("assets/textures/gear.png")
        iconMap["apply_material"] = loader.getImage("assets/textures/apply_material.png")
        iconMap["load_material"] = loader.getImage("assets/textures/load_material.png")
        iconMap["duplicate_material"] = loader.getImage("assets/textures/duplicate_material.png")
        iconMap["add_material"] = loader.getImage("assets/textures/add_material.png")
        iconMap["remove_material"] = loader.getImage("assets/textures/remove_material.png")
        iconMap["material"] = loader.getImage("assets/textures/material.png")

        iconMap["addTemplateCubeIcon"] = loader.getImage("assets/textures/add_template_cube.png")
        iconMap["addMeshCubeIcon"] = loader.getImage("assets/textures/add_mesh_cube.png")
        iconMap["newProjectIcon"] = loader.getImage("assets/textures/new_project.png")
        iconMap["loadProjectCubeIcon"] = loader.getImage("assets/textures/load_project.png")
        iconMap["saveProjectIcon"] = loader.getImage("assets/textures/save_project.png")
        iconMap["saveAsProjectIcon"] = loader.getImage("assets/textures/save_as_project.png")
        iconMap["editProjectIcon"] = loader.getImage("assets/textures/edit_project.png")
        iconMap["importModelIcon"] = loader.getImage("assets/textures/import_model.png")
        iconMap["exportModelIcon"] = loader.getImage("assets/textures/export_model.png")
        iconMap["exportTextureIcon"] = loader.getImage("assets/textures/export_texture.png")
        iconMap["exportHitboxIcon"] = loader.getImage("assets/textures/export_hitbox.png")
        iconMap["active_grid"] = loader.getImage("assets/textures/show_grids.png")
        iconMap["disable_grid"] = loader.getImage("assets/textures/hide_grids.png")
        iconMap["active_model_grid"] = loader.getImage("assets/textures/active_model_grids.png")
        iconMap["active_color"] = loader.getImage("assets/textures/active_color.png")
        iconMap["active_light"] = loader.getImage("assets/textures/active_light.png")
        iconMap["active_focus"] = loader.getImage("assets/textures/active_focus.png")
        iconMap["active_invisible"] = loader.getImage("assets/textures/active_invisible.png")
        iconMap["moveToGroupIcon"] = loader.getImage("assets/textures/move_to_group.png")

        iconMap["button_up"] = loader.getImage("assets/textures/up.png")
        iconMap["button_down"] = loader.getImage("assets/textures/down.png")
        iconMap["button_left"] = loader.getImage("assets/textures/left.png")
        iconMap["button_right"] = loader.getImage("assets/textures/right.png")
        iconMap["button_right_dark"] = loader.getImage("assets/textures/dot.png")

        iconMap["obj_type_cube"] = loader.getImage("assets/textures/obj_type_cube.png")
        iconMap["obj_type_mesh"] = loader.getImage("assets/textures/obj_type_mesh.png")
        iconMap["group_icon"] = loader.getImage("assets/textures/group_icon.png")
        iconMap["material_in_use"] = loader.getImage("assets/textures/material_in_use.png")

        iconMap["seek_start"] = loader.getImage("assets/textures/seek_start.png")
        iconMap["prev_keyframe"] = loader.getImage("assets/textures/prev_keyframe.png")
        iconMap["play_reversed"] = loader.getImage("assets/textures/left.png")
        iconMap["play_normal"] = loader.getImage("assets/textures/right.png")
        iconMap["play_pause"] = loader.getImage("assets/textures/play_pause.png")
        iconMap["next_keyframe"] = loader.getImage("assets/textures/next_keyframe.png")
        iconMap["seek_end"] = loader.getImage("assets/textures/seek_end.png")
        iconMap["add_keyframe"] = loader.getImage("assets/textures/add_keyframe.png")
        iconMap["remove_keyframe"] = loader.getImage("assets/textures/remove_keyframe.png")
        iconMap["dup_animation"] = loader.getImage("assets/textures/duplicate_material.png")
        iconMap["add_animation"] = loader.getImage("assets/textures/add.png")
        iconMap["remove_animation"] = loader.getImage("assets/textures/remove.png")

        iconMap["active_selection_mode_object"] = loader.getImage("assets/textures/selection_mode_object.png")
        iconMap["active_selection_mode_face"] = loader.getImage("assets/textures/selection_mode_face.png")
        iconMap["active_selection_mode_edge"] = loader.getImage("assets/textures/selection_mode_edge.png")
        iconMap["active_selection_mode_vertex"] = loader.getImage("assets/textures/selection_mode_vertex.png")

        iconMap["active_selection_mode_translation"] = loader.getImage("assets/textures/translation.png")
        iconMap["active_selection_mode_rotation"] = loader.getImage("assets/textures/rotation.png")
        iconMap["active_selection_mode_scale"] = loader.getImage("assets/textures/scale.png")

        iconMap["active_selection_orientation_local"] = loader.getImage("assets/textures/local_orientation.png")
        iconMap["active_selection_orientation_global"] = loader.getImage("assets/textures/global_orientation.png")

        iconMap["add_channel"] = loader.getImage("assets/textures/add.png")
        iconMap["remove_channel"] = loader.getImage("assets/textures/remove.png")
        iconMap["picker"] = loader.getImage("assets/textures/picker.png")
        iconMap["spread_value"] = loader.getImage("assets/textures/spread.png")

        iconMap["missing"] = loader.getImage("assets/textures/missing.png")

        MaterialNone.loadTexture(loader)
        log(Level.FINE) { "[GuiResources] Gui resources loaded" }
    }

    fun getIcon(name: String): StbBackedLoadableImage = iconMap[name] ?: iconMap["missing"]!!
    fun getIconOrNull(name: String): StbBackedLoadableImage? = iconMap[name]
}