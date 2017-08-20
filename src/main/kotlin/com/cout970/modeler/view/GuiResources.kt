package com.cout970.modeler.view

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

    lateinit var baseCubeTexture: Texture
    lateinit var centerMarkTexture: Texture

    lateinit var deleteIcon: BufferedImage
    lateinit var showIcon: BufferedImage
    lateinit var hideIcon: BufferedImage
    lateinit var applyMaterial: BufferedImage
    lateinit var loadMaterial: BufferedImage
    lateinit var addTemplateCubeIcon: BufferedImage
    lateinit var addMeshCubeIcon: BufferedImage

    lateinit var newProjectIcon: BufferedImage
    lateinit var loadProjectCubeIcon: BufferedImage
    lateinit var saveProjectIcon: BufferedImage
    lateinit var saveAsProjectIcon: BufferedImage
    lateinit var editProjectIcon: BufferedImage

    lateinit var importModelIcon: BufferedImage
    lateinit var exportModelIcon: BufferedImage
    lateinit var exportTextureIcon: BufferedImage
    lateinit var exportHitboxIcon: BufferedImage

    lateinit var showGridsIcon: BufferedImage
    lateinit var hideGridsIcon: BufferedImage

    lateinit var upIcon: BufferedImage
    lateinit var downIcon: BufferedImage


    fun reload(loader: ResourceLoader) {
        log(Level.FINE) { "[GuiResources] Loading gui resources" }
        baseCubeMesh = ModelImporters.objImporter.importAsMesh("assets/models/cube.obj".fromClasspath(), true)
        lightMesh = ModelImporters.objImporter.importAsMesh("assets/models/light.obj".fromClasspath(), true)
        translationArrow = ModelImporters.objImporter.importAsMesh("assets/models/translation_x.obj".fromClasspath(),
                true)

        baseCubeTexture = loader.getTexture("assets/textures/models/cube.png").apply { magFilter = Texture.PIXELATED }
        centerMarkTexture = loader.getTexture("assets/textures/models/center_mark.png")

        deleteIcon = BufferedImage("assets/textures/delete.png")
        showIcon = BufferedImage("assets/textures/show.png")
        hideIcon = BufferedImage("assets/textures/hide.png")
        applyMaterial = BufferedImage("assets/textures/apply_material.png")
        loadMaterial = BufferedImage("assets/textures/load_material.png")
        addTemplateCubeIcon = BufferedImage("assets/textures/add_template_cube.png")
        addMeshCubeIcon = BufferedImage("assets/textures/add_mesh_cube.png")
        newProjectIcon = BufferedImage("assets/textures/new_project.png")
        loadProjectCubeIcon = BufferedImage("assets/textures/load_project.png")
        saveProjectIcon = BufferedImage("assets/textures/save_project.png")
        saveAsProjectIcon = BufferedImage("assets/textures/save_as_project.png")
        editProjectIcon = BufferedImage("assets/textures/edit_project.png")
        importModelIcon = BufferedImage("assets/textures/import_model.png")
        exportModelIcon = BufferedImage("assets/textures/export_model.png")
        exportTextureIcon = BufferedImage("assets/textures/export_texture.png")
        exportHitboxIcon = BufferedImage("assets/textures/export_hitbox.png")
        showGridsIcon = BufferedImage("assets/textures/show_grids.png")
        hideGridsIcon = BufferedImage("assets/textures/hide_grids.png")
        upIcon = BufferedImage("assets/textures/up.png")
        downIcon = BufferedImage("assets/textures/down.png")

        MaterialNone.loadTexture(loader)
        log(Level.FINE) { "[GuiResources] Gui resources loaded" }
    }
}