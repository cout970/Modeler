package com.cout970.modeler.gui

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.fromClasspath
import com.cout970.modeler.gui.leguicomp.LogFreeBufferedImage as BufferedImage

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

    val iconMap = mutableMapOf<String, BufferedImage>()

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
        orientationCube = loader.getTexture("assets/textures/models/orientation_cube.png").apply { magFilter = Texture.PIXELATED }
        centerMarkTexture = loader.getTexture("assets/textures/models/center_mark.png")
        skyboxTexture = loader.getTextureCubeMap("assets/textures/models/skybox").apply {
            magFilter = Texture.SMOOTH
            minFilter = Texture.SMOOTH
            wrapT = Texture.CLAMP_TO_EDGE
            wrapS = Texture.CLAMP_TO_EDGE
            wrapW = Texture.CLAMP_TO_EDGE
        }

        iconMap["deleteIcon"] = BufferedImage("assets/textures/delete.png")
        iconMap["showIcon"] = BufferedImage("assets/textures/show.png")
        iconMap["hideIcon"] = BufferedImage("assets/textures/hide.png")

        iconMap["add_color_material"] = BufferedImage("assets/textures/add_colored_material.png")
        iconMap["config_material"] = BufferedImage("assets/textures/gear.png")
        iconMap["apply_material"] = BufferedImage("assets/textures/apply_material.png")
        iconMap["load_material"] = BufferedImage("assets/textures/load_material.png")
        iconMap["duplicate_material"] = BufferedImage("assets/textures/duplicate_material.png")
        iconMap["add_material"] = BufferedImage("assets/textures/add_material.png")
        iconMap["remove_material"] = BufferedImage("assets/textures/remove_material.png")
        iconMap["material"] = BufferedImage("assets/textures/material.png")

        iconMap["addTemplateCubeIcon"] = BufferedImage("assets/textures/add_template_cube.png")
        iconMap["addMeshCubeIcon"] = BufferedImage("assets/textures/add_mesh_cube.png")
        iconMap["newProjectIcon"] = BufferedImage("assets/textures/new_project.png")
        iconMap["loadProjectCubeIcon"] = BufferedImage("assets/textures/load_project.png")
        iconMap["saveProjectIcon"] = BufferedImage("assets/textures/save_project.png")
        iconMap["saveAsProjectIcon"] = BufferedImage("assets/textures/save_as_project.png")
        iconMap["editProjectIcon"] = BufferedImage("assets/textures/edit_project.png")
        iconMap["importModelIcon"] = BufferedImage("assets/textures/import_model.png")
        iconMap["exportModelIcon"] = BufferedImage("assets/textures/export_model.png")
        iconMap["exportTextureIcon"] = BufferedImage("assets/textures/export_texture.png")
        iconMap["exportHitboxIcon"] = BufferedImage("assets/textures/export_hitbox.png")
        iconMap["active_grid"] = BufferedImage("assets/textures/show_grids.png")
        iconMap["disable_grid"] = BufferedImage("assets/textures/hide_grids.png")
        iconMap["active_model_grid"] = BufferedImage("assets/textures/active_model_grids.png")
        iconMap["active_color"] = BufferedImage("assets/textures/active_color.png")
        iconMap["active_light"] = BufferedImage("assets/textures/active_light.png")
        iconMap["active_focus"] = BufferedImage("assets/textures/active_focus.png")
        iconMap["active_invisible"] = BufferedImage("assets/textures/active_invisible.png")

        iconMap["button_up"] = BufferedImage("assets/textures/up.png")
        iconMap["button_down"] = BufferedImage("assets/textures/down.png")
        iconMap["button_left"] = BufferedImage("assets/textures/left.png")
        iconMap["button_right"] = BufferedImage("assets/textures/right.png")
        iconMap["button_right_dark"] = BufferedImage("assets/textures/dot.png")

        iconMap["obj_type_cube"] = BufferedImage("assets/textures/obj_type_cube.png")
        iconMap["obj_type_mesh"] = BufferedImage("assets/textures/obj_type_mesh.png")
        iconMap["group_icon"] = BufferedImage("assets/textures/group_icon.png")
        iconMap["material_in_use"] = BufferedImage("assets/textures/material_in_use.png")

        iconMap["seek_start"] = BufferedImage("assets/textures/seek_start.png")
        iconMap["prev_keyframe"] = BufferedImage("assets/textures/prev_keyframe.png")
        iconMap["play_reversed"] = BufferedImage("assets/textures/left.png")
        iconMap["play_normal"] = BufferedImage("assets/textures/right.png")
        iconMap["play_pause"] = BufferedImage("assets/textures/play_pause.png")
        iconMap["next_keyframe"] = BufferedImage("assets/textures/next_keyframe.png")
        iconMap["seek_end"] = BufferedImage("assets/textures/seek_end.png")
        iconMap["add_keyframe"] = BufferedImage("assets/textures/add_keyframe.png")
        iconMap["remove_keyframe"] = BufferedImage("assets/textures/remove_keyframe.png")
        iconMap["dup_animation"] = BufferedImage("assets/textures/duplicate_material.png")
        iconMap["add_animation"] = BufferedImage("assets/textures/add.png")
        iconMap["remove_animation"] = BufferedImage("assets/textures/remove.png")

        iconMap["active_selection_mode_object"] = BufferedImage("assets/textures/selection_mode_object.png")
        iconMap["active_selection_mode_face"] = BufferedImage("assets/textures/selection_mode_face.png")
        iconMap["active_selection_mode_edge"] = BufferedImage("assets/textures/selection_mode_edge.png")
        iconMap["active_selection_mode_vertex"] = BufferedImage("assets/textures/selection_mode_vertex.png")

        iconMap["active_selection_mode_translation"] = BufferedImage("assets/textures/translation.png")
        iconMap["active_selection_mode_rotation"] = BufferedImage("assets/textures/rotation.png")
        iconMap["active_selection_mode_scale"] = BufferedImage("assets/textures/scale.png")

        iconMap["active_selection_orientation_local"] = BufferedImage("assets/textures/local_orientation.png")
        iconMap["active_selection_orientation_global"] = BufferedImage("assets/textures/global_orientation.png")

        iconMap["add_channel"] = BufferedImage("assets/textures/add.png")
        iconMap["remove_channel"] = BufferedImage("assets/textures/remove.png")
        iconMap["picker"] = BufferedImage("assets/textures/picker.png")
        iconMap["spread_value"] = BufferedImage("assets/textures/spread.png")

        iconMap["missing"] = BufferedImage("assets/textures/missing.png")

        MaterialNone.loadTexture(loader)
        log(Level.FINE) { "[GuiResources] Gui resources loaded" }
    }

    fun getIcon(name: String): BufferedImage = iconMap[name] ?: iconMap["missing"]!!
    fun getIconOrNull(name: String): BufferedImage? = iconMap[name]
}