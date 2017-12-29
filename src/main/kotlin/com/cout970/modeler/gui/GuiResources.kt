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
    lateinit var lightMesh: IMesh
    lateinit var translationArrow: IMesh
    lateinit var rotationRing: IMesh
    lateinit var scaleArrow: IMesh
    lateinit var skybox: IMesh

    lateinit var baseCubeTexture: Texture
    lateinit var centerMarkTexture: Texture
    lateinit var skyboxTexture: Texture

    val iconMap = mutableMapOf<String, BufferedImage>()

    fun reload(loader: ResourceLoader) {
        log(Level.FINE) { "[GuiResources] Loading gui resources" }
        baseCubeMesh = ModelImporters.objImporter.importAsMesh("assets/models/cube.obj".fromClasspath(), true)
        lightMesh = ModelImporters.objImporter.importAsMesh("assets/models/light.obj".fromClasspath(), true)
        translationArrow = ModelImporters.objImporter.importAsMesh("assets/models/translation_x.obj".fromClasspath(),
                true)
        rotationRing = ModelImporters.objImporter.importAsMesh("assets/models/rotation_x.obj".fromClasspath(), true)
        scaleArrow = ModelImporters.objImporter.importAsMesh("assets/models/scale_x.obj".fromClasspath(), true)
        skybox = ModelImporters.objImporter.importAsMesh("assets/models/skybox.obj".fromClasspath(), false)

        baseCubeTexture = loader.getTexture("assets/textures/models/cube.png").apply { magFilter = Texture.PIXELATED }
        centerMarkTexture = loader.getTexture("assets/textures/models/center_mark.png")
        skyboxTexture = loader.getTextureCubeMap("assets/textures/models/skybox").apply {
            magFilter = Texture.SMOOTH
            minFilter = Texture.SMOOTH
            wrapT = Texture.CLAMP_TO_EDGE
            wrapS = Texture.CLAMP_TO_EDGE
            wrapW = Texture.CLAMP_TO_EDGE
        }

        iconMap.put("deleteIcon", BufferedImage("assets/textures/delete.png"))
        iconMap.put("showIcon", BufferedImage("assets/textures/show.png"))
        iconMap.put("hideIcon", BufferedImage("assets/textures/hide.png"))
        iconMap.put("applyMaterial", BufferedImage("assets/textures/apply_material.png"))
        iconMap.put("loadMaterial", BufferedImage("assets/textures/load_material.png"))
        iconMap.put("addMaterialIcon", BufferedImage("assets/textures/add_material.png"))
        iconMap.put("removeMaterialIcon", BufferedImage("assets/textures/remove_material.png"))
        iconMap.put("addTemplateCubeIcon", BufferedImage("assets/textures/add_template_cube.png"))
        iconMap.put("addMeshCubeIcon", BufferedImage("assets/textures/add_mesh_cube.png"))
        iconMap.put("newProjectIcon", BufferedImage("assets/textures/new_project.png"))
        iconMap.put("loadProjectCubeIcon", BufferedImage("assets/textures/load_project.png"))
        iconMap.put("saveProjectIcon", BufferedImage("assets/textures/save_project.png"))
        iconMap.put("saveAsProjectIcon", BufferedImage("assets/textures/save_as_project.png"))
        iconMap.put("editProjectIcon", BufferedImage("assets/textures/edit_project.png"))
        iconMap.put("importModelIcon", BufferedImage("assets/textures/import_model.png"))
        iconMap.put("exportModelIcon", BufferedImage("assets/textures/export_model.png"))
        iconMap.put("exportTextureIcon", BufferedImage("assets/textures/export_texture.png"))
        iconMap.put("exportHitboxIcon", BufferedImage("assets/textures/export_hitbox.png"))
        iconMap.put("active_grid", BufferedImage("assets/textures/show_grids.png"))
        iconMap.put("disable_grid", BufferedImage("assets/textures/hide_grids.png"))
        iconMap.put("active_model_grid", BufferedImage("assets/textures/active_model_grids.png"))
        iconMap.put("active_color", BufferedImage("assets/textures/active_color.png"))
        iconMap.put("active_light", BufferedImage("assets/textures/active_light.png"))
        iconMap.put("active_texture", BufferedImage("assets/textures/active_texture.png"))
        iconMap.put("active_focus", BufferedImage("assets/textures/active_focus.png"))
        iconMap.put("active_invisible", BufferedImage("assets/textures/active_invisible.png"))
        iconMap.put("button_up", BufferedImage("assets/textures/up.png"))
        iconMap.put("button_down", BufferedImage("assets/textures/down.png"))
        iconMap.put("button_left", BufferedImage("assets/textures/left.png"))
        iconMap.put("button_right", BufferedImage("assets/textures/right.png"))
        iconMap.put("active_selection_mode_object", BufferedImage("assets/textures/selection_mode_object.png"))
        iconMap.put("active_selection_mode_face", BufferedImage("assets/textures/selection_mode_face.png"))
        iconMap.put("active_selection_mode_edge", BufferedImage("assets/textures/selection_mode_edge.png"))
        iconMap.put("active_selection_mode_vertex", BufferedImage("assets/textures/selection_mode_vertex.png"))

        MaterialNone.loadTexture(loader)
        log(Level.FINE) { "[GuiResources] Gui resources loaded" }
    }

    fun getIcon(name: String): BufferedImage? = iconMap[name]
}