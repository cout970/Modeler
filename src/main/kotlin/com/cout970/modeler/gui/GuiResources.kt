package com.cout970.modeler.gui

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.export.ModelImporters
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.core.resource.fromClasspath
import org.liquidengine.legui.image.BufferedImage

/**
 * Created by cout970 on 2017/06/14.
 */
class GuiResources {

    lateinit var baseCubeMesh: IMesh
    lateinit var lightMesh: IMesh
    lateinit var translationArrow: IMesh
    lateinit var rotationRing: IMesh
    lateinit var scaleArrow: IMesh

    lateinit var baseCubeTexture: Texture
    lateinit var centerMarkTexture: Texture

    val iconMap = mutableMapOf<String, BufferedImage>()


    fun reload(loader: ResourceLoader) {
        log(Level.FINE) { "[GuiResources] Loading gui resources" }
        baseCubeMesh = ModelImporters.objImporter.importAsMesh("assets/models/cube.obj".fromClasspath(), true)
        lightMesh = ModelImporters.objImporter.importAsMesh("assets/models/light.obj".fromClasspath(), true)
        translationArrow = ModelImporters.objImporter.importAsMesh("assets/models/translation_x.obj".fromClasspath(),
                true)
        rotationRing = ModelImporters.objImporter.importAsMesh("assets/models/rotation_x.obj".fromClasspath(), true)
        scaleArrow = ModelImporters.objImporter.importAsMesh("assets/models/scale_x.obj".fromClasspath(), true)

        baseCubeTexture = loader.getTexture("assets/textures/models/cube.png").apply { magFilter = Texture.PIXELATED }
        centerMarkTexture = loader.getTexture("assets/textures/models/center_mark.png")

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
        iconMap.put("disable_grid", BufferedImage("assets/textures/show_grids.png"))
        iconMap.put("active_grid", BufferedImage("assets/textures/hide_grids.png"))
        iconMap.put("upIcon", BufferedImage("assets/textures/up.png"))
        iconMap.put("downIcon", BufferedImage("assets/textures/down.png"))

        MaterialNone.loadTexture(loader)
        log(Level.FINE) { "[GuiResources] Gui resources loaded" }
    }

    fun getIcon(name: String): BufferedImage? = iconMap[name]
}